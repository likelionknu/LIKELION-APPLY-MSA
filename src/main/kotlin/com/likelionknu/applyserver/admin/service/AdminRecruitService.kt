package com.likelionknu.applyserver.admin.service

import com.likelionknu.applyserver.admin.data.dto.request.AdminRecruitCreateRequest
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitDetailResponse
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitListResponse
import com.likelionknu.applyserver.application.data.exception.RecruitNotFoundException
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.auth.data.enums.ApplicationEvaluation
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException
import com.likelionknu.applyserver.recruit.data.entity.Recruit
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminRecruitService(
    private val recruitRepository: RecruitRepository,
    private val recruitContentRepository: RecruitContentRepository,
    private val applicationRepository: ApplicationRepository
) {

    @Transactional(readOnly = true)
    fun getRecruits(): List<AdminRecruitListResponse> {
        return recruitRepository.findAllByDeletedAtIsNull()
            .map { recruit ->
                val recruitId = recruit.id
                    ?: throw IllegalStateException("모집 공고 ID가 없습니다.")

                AdminRecruitListResponse.from(
                    recruit = recruit,
                    questionCount = recruitContentRepository.countByRecruitId(recruitId),
                    submit = applicationRepository.countByRecruitIdAndStatus(
                        recruitId,
                        ApplicationStatus.SUBMITTED
                    ),
                    draft = applicationRepository.countByRecruitIdAndStatus(
                        recruitId,
                        ApplicationStatus.DRAFT
                    ),
                    cancel = applicationRepository.countByRecruitIdAndStatus(
                        recruitId,
                        ApplicationStatus.CANCELED
                    ),
                    pass = applicationRepository.countByRecruitIdAndStatusAndEvaluation(
                        recruitId,
                        ApplicationStatus.SUBMITTED,
                        ApplicationEvaluation.PASS
                    ),
                    fail = applicationRepository.countByRecruitIdAndStatusAndEvaluation(
                        recruitId,
                        ApplicationStatus.SUBMITTED,
                        ApplicationEvaluation.FAIL
                    ),
                    hold = applicationRepository.countByRecruitIdAndStatusAndEvaluation(
                        recruitId,
                        ApplicationStatus.SUBMITTED,
                        ApplicationEvaluation.HOLD
                    ),
                    notReviewed = applicationRepository
                        .countByRecruitIdAndStatusAndEvaluationIsNull(
                            recruitId,
                            ApplicationStatus.SUBMITTED
                        )
                )
            }
    }

    @Transactional(readOnly = true)
    fun getRecruit(recruitId: Long): AdminRecruitDetailResponse {
        val recruit = findActiveRecruit(recruitId)

        val contents = recruitContentRepository
            .findByRecruitIdOrderByPriorityAsc(recruitId)

        return AdminRecruitDetailResponse.from(
            recruit = recruit,
            contents = contents
        )
    }

    @Transactional
    fun createRecruit(request: AdminRecruitCreateRequest): Long {
        validateRecruitRequest(request)

        val recruit = recruitRepository.save(
            Recruit(
                title = request.title.trim(),
                generation = request.generation,
                startAt = request.startAt ?: throw invalidRequest(),
                endAt = request.endAt ?: throw invalidRequest(),
                documentResultAt = request.documentResultAt
                    ?: throw invalidRequest(),
                finalResultAt = request.finalResultAt
                    ?: throw invalidRequest()
            )
        )

        recruitContentRepository.saveAll(
            createRecruitContents(recruit, request)
        )

        return recruit.id
            ?: throw IllegalStateException("모집 공고 ID가 생성되지 않았습니다.")
    }

    @Transactional
    fun updateRecruit(
        recruitId: Long,
        request: AdminRecruitCreateRequest
    ) {
        validateRecruitRequest(request)

        val recruit = findActiveRecruit(recruitId)

        if (applicationRepository.existsByRecruitId(recruitId)) {
            throw GlobalException(ErrorCode.CONFLICT)
        }

        recruit.update(
            title = request.title.trim(),
            generation = request.generation,
            startAt = request.startAt ?: throw invalidRequest(),
            endAt = request.endAt ?: throw invalidRequest(),
            documentResultAt = request.documentResultAt
                ?: throw invalidRequest(),
            finalResultAt = request.finalResultAt
                ?: throw invalidRequest()
        )

        recruitContentRepository.deleteAllByRecruitId(recruitId)

        recruitContentRepository.saveAll(
            createRecruitContents(recruit, request)
        )
    }

    @Transactional
    fun deleteRecruit(recruitId: Long) {
        val recruit = findActiveRecruit(recruitId)

        if (applicationRepository.existsByRecruitId(recruitId)) {
            throw GlobalException(ErrorCode.CONFLICT)
        }

        recruit.softDelete()
    }

    private fun createRecruitContents(
        recruit: Recruit,
        request: AdminRecruitCreateRequest
    ): List<RecruitContent> {
        return request.questions
            .sortedBy { it.priority }
            .map { question ->
                RecruitContent(
                    recruit = recruit,
                    question = question.question.trim(),
                    priority = question.priority,
                    required = question.required,
                    minLength = question.minLength,
                    maxLength = question.maxLength
                )
            }
    }

    private fun findActiveRecruit(recruitId: Long): Recruit {
        val recruit = recruitRepository.findById(recruitId)
            .orElseThrow { RecruitNotFoundException() }

        if (recruit.deletedAt != null) {
            throw RecruitNotFoundException()
        }

        return recruit
    }

    private fun validateRecruitRequest(
        request: AdminRecruitCreateRequest
    ) {
        val startAt = request.startAt ?: throw invalidRequest()
        val endAt = request.endAt ?: throw invalidRequest()
        val documentResultAt = request.documentResultAt
            ?: throw invalidRequest()
        val finalResultAt = request.finalResultAt
            ?: throw invalidRequest()

        if (!startAt.isBefore(endAt)) {
            throw invalidRequest()
        }

        if (!endAt.isBefore(documentResultAt)) {
            throw invalidRequest()
        }

        if (!documentResultAt.isBefore(finalResultAt)) {
            throw invalidRequest()
        }

        val priorities = request.questions.map { it.priority }
        val expectedPriorities = (1..request.questions.size).toList()

        if (priorities.sorted() != expectedPriorities) {
            throw invalidRequest()
        }

        request.questions.forEach { question ->
            val minLength = question.minLength ?: throw invalidRequest()
            val maxLength = question.maxLength ?: throw invalidRequest()

            if (minLength > maxLength) {
                throw invalidRequest()
            }
        }
    }

    private fun invalidRequest(): GlobalException {
        return GlobalException(ErrorCode.INVALID_REQUEST)
    }
}