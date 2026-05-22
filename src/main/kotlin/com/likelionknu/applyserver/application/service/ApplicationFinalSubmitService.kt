package com.likelionknu.applyserver.application.service

import com.likelionknu.applyserver.application.data.dto.request.FinalSubmitRequestDto
import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer
import com.likelionknu.applyserver.application.data.exception.EmptyAnswerException
import com.likelionknu.applyserver.application.data.exception.InvalidApplicationQuestionException
import com.likelionknu.applyserver.application.data.exception.ProfileIncompleteException
import com.likelionknu.applyserver.application.data.exception.RecruitContentNotFoundException
import com.likelionknu.applyserver.application.data.exception.RecruitIsNotOpenedException
import com.likelionknu.applyserver.application.data.exception.RecruitNotFoundException
import com.likelionknu.applyserver.application.data.exception.UserNotFoundException
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.discord.service.DiscordNotificationService
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import com.likelionknu.applyserver.user.data.repository.ApplyUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ApplicationFinalSubmitService(
    private val applicationRepository: ApplicationRepository,
    private val recruitAnswerRepository: RecruitAnswerRepository,
    private val recruitContentRepository: RecruitContentRepository,
    private val applyUserRepository: ApplyUserRepository,
    private val recruitRepository: RecruitRepository,
    private val discordNotificationService: DiscordNotificationService
) {
    fun finalSubmit(
        email: String,
        request: FinalSubmitRequestDto
    ) {
        if (request.items.isEmpty()) {
            throw EmptyAnswerException()
        }

        val applyUser = applyUserRepository.findByEmail(email)
            ?: throw UserNotFoundException()

        val applyUserId = applyUser.id
            ?: throw UserNotFoundException()

        val profileCompleted =
            applyUser.studentId != null &&
                    applyUser.depart != null &&
                    applyUser.phone != null &&
                    applyUser.grade != null &&
                    applyUser.status != null

        if (!profileCompleted) {
            throw ProfileIncompleteException()
        }

        val recruit = recruitRepository.findById(request.recruitId)
            .orElseThrow { RecruitNotFoundException() }

        val recruitId = recruit.id
            ?: throw RecruitNotFoundException()

        val now = LocalDateTime.now()
        val isOpen = !now.isBefore(recruit.startAt) && !now.isAfter(recruit.endAt)

        if (!isOpen) {
            throw RecruitIsNotOpenedException()
        }

        val existingApplication = applicationRepository.findByUserIdAndRecruitId(
            applyUserId,
            request.recruitId
        )

        if (existingApplication != null && existingApplication.status != ApplicationStatus.DRAFT) {
            throw IllegalStateException(
                "이미 제출되었거나 처리 중인 지원서는 최종 제출을 다시 할 수 없습니다. 회수 취소(restore)만 가능합니다."
            )
        }

        val application = existingApplication ?: applicationRepository.save(
            Application(
                recruit = recruit,
                user = applyUser,
                status = ApplicationStatus.DRAFT,
                submittedAt = now
            )
        )

        val applicationId = application.id
            ?: throw IllegalStateException("지원서 ID가 없습니다.")

        val requiredContents = recruitContentRepository.findAllByRecruit_IdAndRequiredTrue(recruitId)
        val submittedQuestionIds = request.items.map { it.questionId }.toSet()

        requiredContents.forEach { required ->
            if (required.id !in submittedQuestionIds) {
                throw EmptyAnswerException()
            }
        }

        recruitAnswerRepository.deleteByApplication_Id(applicationId)

        val answers = request.items.map { item ->
            val content = recruitContentRepository.findById(item.questionId)
                .orElseThrow { RecruitContentNotFoundException() }

            if (request.recruitId != content.recruit?.id) {
                throw InvalidApplicationQuestionException()
            }

            RecruitAnswer(
                application = application,
                content = content,
                answer = item.answer
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
}