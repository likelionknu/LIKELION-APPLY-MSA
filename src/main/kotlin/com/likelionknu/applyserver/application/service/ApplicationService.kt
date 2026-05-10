package com.likelionknu.applyserver.application.service

import com.likelionknu.applyserver.application.data.dto.request.ApplicationDraftSaveRequest
import com.likelionknu.applyserver.application.data.dto.response.ApplicationAnswerResponseDto
import com.likelionknu.applyserver.application.data.dto.response.ApplicationInfoResponseDto
import com.likelionknu.applyserver.application.data.exception.ApplicationNotFoundException
import com.likelionknu.applyserver.application.data.exception.ApplicationStateException
import com.likelionknu.applyserver.application.data.exception.UserNotFoundException
import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.auth.data.repository.UserRepository
import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException
import com.likelionknu.applyserver.discord.service.DiscordNotificationService
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ApplicationService(
    private val applicationRepository: ApplicationRepository,
    private val applicationAnswerService: ApplicationAnswerService,
    private val discordNotificationService: DiscordNotificationService,
    private val userRepository: UserRepository,
    private val recruitRepository: RecruitRepository
) {
    /**
     * 지원서 임시 저장
     *
     * 모집 공고가 모집 상태일 때에만 임시 저장 가능
     * 기존 지원서가 없다면 DRAFT 상태로 지원서를 생성함
     *
     * @param userId: 사용자 고유 ID
     * @param recruitId: 모집 공고 고유 ID
     * @param requests: 임시 저장할 답변 목록
     * @return (임시 저장한) 지원서 ID
     *
     * @throws ApplicationNotFoundException 모집 공고를 찾을 수 없을 경우
     * @throws ApplicationStateException 현재 지원서 상태가 DRAFT(임시 저장) 상태가 아닐 때
     * @throws GlobalException 모집 공고가 모집 상태가 아닐 경우
     */
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

        if (!isOpen) throw GlobalException(ErrorCode.FORBIDDEN)

        val application = applicationRepository
            .findByUserIdAndRecruitId(userId, recruitId)
            ?: applicationRepository.save(
                Application(
                    user = userRepository.findById(userId)
                        .orElseThrow { UserNotFoundException() },
                    recruit = recruit,
                    status = ApplicationStatus.DRAFT,
                    submittedAt = now,
                )
            )

        if (application.status != ApplicationStatus.DRAFT) throw ApplicationStateException()

        applicationAnswerService.replaceAnswers(application, requests)
        application.submittedAt = LocalDateTime.now()

        discordNotificationService.sendUserDraftApplication(
            application.user.name,
            application.user.email,
            application.recruit.title
        )

        return application.id ?: throw ApplicationNotFoundException()
    }

    /**
     * 지원서 정보 조회
     *
     * @param applicationId: 지원서 고유 ID
     * @return 지원서 정보(ApplicationInfoResponseDto)
     *
     * @throws ApplicationNotFoundException
     */
    @Transactional
    fun getApplicationInfo(applicationId: Long): ApplicationInfoResponseDto {
        val application = applicationRepository.findById(applicationId)
            .orElseThrow { ApplicationNotFoundException() }

        val answers = applicationAnswerService.getAnswers(application).map { recruitAnswer ->
            ApplicationAnswerResponseDto(
                question = recruitAnswer.content.question,
                answer = recruitAnswer.answer
            )
        }

        val profile = application.user.profile ?: throw UserNotFoundException()

        return ApplicationInfoResponseDto(
            name = application.user.name,
            email = application.user.email,
            depart = profile.depart,
            studentId = profile.studentId,
            grade = profile.grade,
            studentStatus = profile.status?.displayName,
            status = application.status.toString(),
            submittedAt = application.submittedAt,
            phone = profile.phone,
            answers = answers
        )
    }
}