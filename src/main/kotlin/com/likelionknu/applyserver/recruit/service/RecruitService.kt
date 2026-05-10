package com.likelionknu.applyserver.recruit.service

import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.auth.data.repository.UserRepository
import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException
import com.likelionknu.applyserver.common.security.SecurityUtil
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitAvailabilityResponse
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitDetailResponse
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitListResponse
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitQuestionResponse
import com.likelionknu.applyserver.recruit.data.entity.Recruit
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class RecruitService (
        private val recruitRepository: RecruitRepository,
        private val recruitContentRepository: RecruitContentRepository,
        private val userRepository: UserRepository,
        private val applicationRepository: ApplicationRepository,
        private val recruitAnswerRepository: RecruitAnswerRepository ) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    fun getRecruits(): List<RecruitListResponse> {
        return recruitRepository.findAll().map(RecruitListResponse::from)
    }

    @Transactional(readOnly = true)
    fun checkAvailability(recruitId: Long): RecruitAvailabilityResponse {
        val email = SecurityUtil.getUsername()
        val user = userRepository.findByEmail(email)
        val recruit: Recruit = recruitRepository.findById(recruitId)
                .orElseThrow { GlobalException(ErrorCode.NOT_FOUND) }

        log.info("[checkAvailability] 모집 공고 지원 가능 여부 조회: {} {}", recruit.title, user.email)

        val now: LocalDateTime = LocalDateTime.now();
        val isOpen: Boolean = !now.isBefore(recruit.startAt) && !now.isAfter(recruit.endAt);

        val existDraft = applicationRepository.existsByUserIdAndRecruitIdAndStatus(user.id!!, recruitId, ApplicationStatus.DRAFT)
        val hasSubmitted = applicationRepository.existsByUserIdAndRecruitIdAndStatusNot(user.id!!, recruitId, ApplicationStatus.DRAFT)

        val profile = user.profile
        val profileCompleted = profile != null &&
                profile.studentId != null &&
                profile.depart != null &&
                profile.phone != null &&
                profile.grade != null &&
                profile.status != null
        val availableApply = isOpen && !hasSubmitted && profileCompleted
        return RecruitAvailabilityResponse(availableApply, existDraft)
    }

    @Transactional(readOnly = true)
    fun getRecruitQuestions(recruitId: Long): RecruitDetailResponse {
        val recruit = recruitRepository.findById(recruitId).orElseThrow { GlobalException(ErrorCode.NOT_FOUND) }
        val now: LocalDateTime = LocalDateTime.now()
        val isOpen: Boolean = !now.isBefore(recruit.startAt) && !now.isAfter(recruit.endAt);
        if (!isOpen) {
            throw GlobalException(ErrorCode.FORBIDDEN)
        }

        val contents: List<RecruitContent> =
                recruitContentRepository.findByRecruitIdOrderByPriorityAsc(recruitId)

        val email = SecurityUtil.getUsername()
        val user = userRepository.findByEmail(email);
        log.info("[getRecruitQuestions] 공고 질문 조회: 공고 ID: {} 요청: {}", recruitId, user.email);

        // 해당 공고에 대한 지원서 조회 (없을 수도 있음)
        val application = applicationRepository.findByUserIdAndRecruitId(user.id!!, recruitId)

        // 답변 구성
        val answerMap = application?.let { app ->
            recruitAnswerRepository.findByApplicationId(app.id!!)
                    .associate { it.content.id to it.answer }
        } ?: emptyMap<Long?, String>()

        // 질문
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
}



