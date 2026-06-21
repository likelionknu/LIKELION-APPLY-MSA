package com.likelionknu.applyserver.application.service

import com.likelionknu.applyserver.application.data.dto.request.ApplicationDraftSaveRequest
import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.application.data.exception.ApplicationNotFoundException
import com.likelionknu.applyserver.application.data.exception.ApplicationStateException
import com.likelionknu.applyserver.application.data.exception.UserNotFoundException
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException
import com.likelionknu.applyserver.discord.service.DiscordNotificationService
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import com.likelionknu.applyserver.user.data.repository.ApplyUserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ApplicationService(
    private val applicationRepository: ApplicationRepository,
    private val applicationAnswerService: ApplicationAnswerService,
    private val applicationAnswerValidator: ApplicationAnswerValidator,
    private val discordNotificationService: DiscordNotificationService,
    private val applyUserRepository: ApplyUserRepository,
    private val recruitRepository: RecruitRepository
) {
    @Transactional
    fun saveDraft(
        userId: Long,
        recruitId: Long,
        requests: List<ApplicationDraftSaveRequest>
    ): Long {
        val recruit = recruitRepository.findById(recruitId)
            .orElseThrow { ApplicationNotFoundException() }

        val now = LocalDateTime.now()
        val isOpen = !now.isBefore(recruit.startAt) && !now.isAfter(recruit.endAt)

        if (!isOpen) {
            throw GlobalException(ErrorCode.FORBIDDEN)
        }

        applicationAnswerValidator.validateDraft(recruitId, requests)

        val application = applicationRepository
            .findByUserIdAndRecruitId(userId, recruitId)
            ?: applicationRepository.save(
                Application(
                    user = applyUserRepository.findById(userId)
                        .orElseThrow { UserNotFoundException() },
                    recruit = recruit,
                    status = ApplicationStatus.DRAFT,
                    submittedAt = now
                )
            )

        if (application.status != ApplicationStatus.DRAFT) {
            throw ApplicationStateException()
        }

        applicationAnswerService.replaceAnswers(application, requests)
        application.submittedAt = LocalDateTime.now()

        discordNotificationService.sendUserDraftApplication(
            application.user.name,
            application.user.email,
            application.recruit.title
        )

        return application.id ?: throw ApplicationNotFoundException()
    }
}