package com.likelionknu.applyserver.application.service

import com.likelionknu.applyserver.application.data.dto.request.FinalSubmitRequestDto
import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer
import com.likelionknu.applyserver.application.data.exception.ProfileIncompleteException
import com.likelionknu.applyserver.application.data.exception.RecruitContentNotFoundException
import com.likelionknu.applyserver.application.data.exception.RecruitIsNotOpenedException
import com.likelionknu.applyserver.application.data.exception.RecruitNotFoundException
import com.likelionknu.applyserver.application.data.exception.UserNotFoundException
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException
import com.likelionknu.applyserver.discord.service.DiscordNotificationService
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import com.likelionknu.applyserver.user.service.ApplyUserSyncService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ApplicationFinalSubmitService(
    private val applicationRepository: ApplicationRepository,
    private val recruitAnswerRepository: RecruitAnswerRepository,
    private val recruitContentRepository: RecruitContentRepository,
    private val recruitRepository: RecruitRepository,
    private val discordNotificationService: DiscordNotificationService,
    private val applyUserSyncService: ApplyUserSyncService,
    private val applicationAnswerValidator: ApplicationAnswerValidator
) {
    fun finalSubmit(
        email: String,
        request: FinalSubmitRequestDto
    ) {
        val applyUser = applyUserSyncService.getOrSync(email)

        val applyUserId = applyUser.id
            ?: throw UserNotFoundException()

        validateProfileCompleted(applyUser)

        val recruit = recruitRepository.findById(request.recruitId)
            .orElseThrow { RecruitNotFoundException() }

        if (recruit.deletedAt != null) {
            throw RecruitNotFoundException()
        }

        val recruitId = recruit.id
            ?: throw RecruitNotFoundException()

        val now = LocalDateTime.now()
        val isOpen = !now.isBefore(recruit.startAt) &&
                !now.isAfter(recruit.endAt)

        if (!isOpen) {
            throw RecruitIsNotOpenedException()
        }

        val preferredPart = request.preferredPart
            ?: throw GlobalException(ErrorCode.INVALID_REQUEST)

        applicationAnswerValidator.validateFinalSubmit(
            recruitId,
            request.items
        )

        val existingApplication =
            applicationRepository.findByUserIdAndRecruitId(
                applyUserId,
                request.recruitId
            )

        if (
            existingApplication != null &&
            existingApplication.status != ApplicationStatus.DRAFT
        ) {
            throw GlobalException(ErrorCode.CONFLICT)
        }

        val application = existingApplication
            ?: applicationRepository.save(
                Application(
                    recruit = recruit,
                    user = applyUser,
                    preferredPart = preferredPart,
                    status = ApplicationStatus.DRAFT,
                    submittedAt = now
                )
            )

        application.updatePreferredPart(preferredPart)

        val applicationId = application.id
            ?: throw IllegalStateException("지원서 ID가 없습니다.")

        recruitAnswerRepository.deleteByApplication_Id(applicationId)

        val answers = request.items.map { item ->
            val content = recruitContentRepository.findById(item.questionId)
                .orElseThrow { RecruitContentNotFoundException() }

            RecruitAnswer(
                application = application,
                content = content,
                answer = item.answer.trim()
            )
        }

        recruitAnswerRepository.saveAll(answers)

        application.submittedAt = LocalDateTime.now()
        application.changeStatus(ApplicationStatus.SUBMITTED)

        discordNotificationService.sendUserSubmittedApplication(
            application.user.name,
            application.user.email,
            application.recruit.title
        )
    }

    private fun validateProfileCompleted(
        applyUser: com.likelionknu.applyserver.user.data.entity.ApplyUser
    ) {
        val profileCompleted =
            applyUser.studentId != null &&
                    applyUser.depart != null &&
                    applyUser.phone != null &&
                    applyUser.grade != null &&
                    applyUser.status != null

        if (!profileCompleted) {
            throw ProfileIncompleteException()
        }
    }
}