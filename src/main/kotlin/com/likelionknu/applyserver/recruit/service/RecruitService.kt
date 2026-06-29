package com.likelionknu.applyserver.recruit.service

import com.likelionknu.applyserver.application.data.exception.UserNotFoundException
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException
import com.likelionknu.applyserver.common.security.SecurityUtil
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitAvailabilityResponse
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitDetailResponse
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitListResponse
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitQuestionResponse
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitResponse
import com.likelionknu.applyserver.recruit.data.entity.Recruit
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import com.likelionknu.applyserver.user.service.ApplyUserSyncService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class RecruitService(
    private val recruitRepository: RecruitRepository,
    private val recruitContentRepository: RecruitContentRepository,
    private val applicationRepository: ApplicationRepository,
    private val recruitAnswerRepository: RecruitAnswerRepository,
    private val applyUserSyncService: ApplyUserSyncService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    fun getRecruits(): List<RecruitListResponse> {
        return recruitRepository.findAllByDeletedAtIsNull()
            .map(RecruitListResponse::from)
    }

    @Transactional(readOnly = true)
    fun getRecruit(recruitId: Long): RecruitResponse {
        val recruit = recruitRepository.findById(recruitId)
            .orElseThrow { GlobalException(ErrorCode.NOT_FOUND) }

        validateRecruitNotDeleted(recruit)

        return RecruitResponse.from(recruit)
    }

    @Transactional
    fun checkAvailability(recruitId: Long): RecruitAvailabilityResponse {
        val email = SecurityUtil.getUsername()
        val applyUser = applyUserSyncService.getOrSync(email)

        val recruit: Recruit = recruitRepository.findById(recruitId)
            .orElseThrow { GlobalException(ErrorCode.NOT_FOUND) }

        validateRecruitNotDeleted(recruit)

        log.info(
            "[checkAvailability] 모집 공고 지원 가능 여부 조회: {} {}",
            recruit.title,
            applyUser.email
        )

        val now = LocalDateTime.now()
        val isOpen = !now.isBefore(recruit.startAt) && !now.isAfter(recruit.endAt)

        val applyUserId = applyUser.id
            ?: throw UserNotFoundException()

        val existDraft = applicationRepository.existsByUserIdAndRecruitIdAndStatus(
            applyUserId,
            recruitId,
            ApplicationStatus.DRAFT
        )

        val hasSubmitted = applicationRepository.existsByUserIdAndRecruitIdAndStatusNot(
            applyUserId,
            recruitId,
            ApplicationStatus.DRAFT
        )

        val profileCompleted =
            applyUser.studentId != null &&
                    applyUser.depart != null &&
                    applyUser.phone != null &&
                    applyUser.grade != null &&
                    applyUser.status != null

        val availableApply = isOpen && !hasSubmitted && profileCompleted

        return RecruitAvailabilityResponse(
            availableApply,
            existDraft
        )
    }

    @Transactional
    fun getRecruitQuestions(recruitId: Long): RecruitDetailResponse {
        val recruit = recruitRepository.findById(recruitId)
            .orElseThrow { GlobalException(ErrorCode.NOT_FOUND) }

        validateRecruitNotDeleted(recruit)

        val contents: List<RecruitContent> =
            recruitContentRepository.findByRecruitIdOrderByPriorityAsc(recruitId)

        val email = SecurityUtil.getUsername()
        val applyUser = applyUserSyncService.getOrSync(email)

        log.info(
            "[getRecruitQuestions] 공고 질문 조회: 공고 ID: {} 요청: {}",
            recruitId,
            applyUser.email
        )

        val applyUserId = applyUser.id
            ?: throw UserNotFoundException()

        val application = applicationRepository.findByUserIdAndRecruitId(
            applyUserId,
            recruitId
        )

        val answerMap = application?.let { app ->
            recruitAnswerRepository.findByApplicationId(app.id!!)
                .associate { it.content.id to it.answer }
        } ?: emptyMap<Long?, String>()

        val questionList = contents.map { content ->
            RecruitQuestionResponse(
                id = content.id,
                question = content.question,
                savedAnswer = answerMap[content.id]
            )
        }

        return RecruitDetailResponse(
            title = recruit.title,
            startAt = recruit.startAt.toString(),
            endAt = recruit.endAt.toString(),
            questions = questionList
        )
    }

    private fun validateRecruitNotDeleted(recruit: Recruit) {
        if (recruit.deletedAt != null) {
            throw GlobalException(ErrorCode.NOT_FOUND)
        }
    }
}