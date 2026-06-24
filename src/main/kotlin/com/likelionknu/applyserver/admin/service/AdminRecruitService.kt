package com.likelionknu.applyserver.admin.service

import com.likelionknu.applyserver.admin.data.dto.request.AdminRecruitCreateRequest
import com.likelionknu.applyserver.admin.data.dto.response.AdminModifyLogResponse
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitDetailResponse
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitListResponse
import com.likelionknu.applyserver.application.data.entity.ApplyModifyLog
import com.likelionknu.applyserver.application.data.exception.RecruitNotFoundException
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.application.data.repository.ApplyModifyLogRepository
import com.likelionknu.applyserver.auth.data.enums.ApplicationEvaluation
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException
import com.likelionknu.applyserver.recruit.data.entity.Recruit
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import com.likelionknu.applyserver.user.data.entity.ApplyUser
import com.likelionknu.applyserver.user.service.ApplyUserSyncService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminRecruitService(
    private val recruitRepository: RecruitRepository,
    private val recruitContentRepository: RecruitContentRepository,
    private val applicationRepository: ApplicationRepository,
    private val applyModifyLogRepository: ApplyModifyLogRepository,
    private val applyUserSyncService: ApplyUserSyncService
) {

    @Transactional(readOnly = true)
    fun getRecruits(): List<AdminRecruitListResponse> {
        return recruitRepository.findAllByDeletedAtIsNull()
            .map { recruit ->
                val recruitId = recruit.id
                    ?: throw IllegalStateException("모집 공고 ID가 없습니다.")

                AdminRecruitListResponse.from(
                    recruit = recruit,
                    questionCount = recruitContentRepository
                        .countByRecruitId(recruitId),
                    submit = applicationRepository
                        .countByRecruitIdAndStatus(
                            recruitId,
                            ApplicationStatus.SUBMITTED
                        ),
                    draft = applicationRepository
                        .countByRecruitIdAndStatus(
                            recruitId,
                            ApplicationStatus.DRAFT
                        ),
                    cancel = applicationRepository
                        .countByRecruitIdAndStatus(
                            recruitId,
                            ApplicationStatus.CANCELED
                        ),
                    pass = applicationRepository
                        .countByRecruitIdAndStatusAndEvaluation(
                            recruitId,
                            ApplicationStatus.SUBMITTED,
                            ApplicationEvaluation.PASS
                        ),
                    fail = applicationRepository
                        .countByRecruitIdAndStatusAndEvaluation(
                            recruitId,
                            ApplicationStatus.SUBMITTED,
                            ApplicationEvaluation.FAIL
                        ),
                    hold = applicationRepository
                        .countByRecruitIdAndStatusAndEvaluation(
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
    fun getRecruit(
        recruitId: Long
    ): AdminRecruitDetailResponse {
        val recruit = findActiveRecruit(recruitId)

        val contents = recruitContentRepository
            .findByRecruitIdOrderByPriorityAsc(recruitId)

        return AdminRecruitDetailResponse.from(
            recruit = recruit,
            contents = contents
        )
    }

    @Transactional(readOnly = true)
    fun getModifyLogs(
        recruitId: Long
    ): List<AdminModifyLogResponse> {
        findActiveRecruit(recruitId)

        return applyModifyLogRepository
            .findAllByRecruitIdWithUserAndApplication(recruitId)
            .map(AdminModifyLogResponse::from)
    }

    @Transactional
    fun createRecruit(
        request: AdminRecruitCreateRequest
    ): Long {
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
            ?: throw IllegalStateException(
                "모집 공고 ID가 생성되지 않았습니다."
            )
    }

    @Transactional
    fun updateRecruit(
        recruitId: Long,
        adminEmail: String,
        request: AdminRecruitCreateRequest
    ) {
        validateRecruitRequest(request)

        val recruit = findActiveRecruit(recruitId)

        val title = request.title.trim()
        val startAt = request.startAt ?: throw invalidRequest()
        val endAt = request.endAt ?: throw invalidRequest()
        val documentResultAt = request.documentResultAt
            ?: throw invalidRequest()
        val finalResultAt = request.finalResultAt
            ?: throw invalidRequest()

        val previousQuestions = recruitContentRepository
            .findByRecruitIdOrderByPriorityAsc(recruitId)

        val questionsChanged =
            previousQuestions.map(::questionFingerprint) !=
                    request.questions
                        .sortedBy { it.priority }
                        .map(::questionFingerprint)

        val hasApplications =
            applicationRepository.existsByRecruitId(recruitId)

        // 기존 답변이 질문을 참조하므로 지원서 생성 후에는 질문을 변경할 수 없다.
        if (hasApplications && questionsChanged) {
            throw GlobalException(ErrorCode.CONFLICT)
        }

        val basicInfoChanged =
            recruit.title != title ||
                    recruit.generation != request.generation ||
                    recruit.startAt != startAt ||
                    recruit.endAt != endAt ||
                    recruit.documentResultAt != documentResultAt ||
                    recruit.finalResultAt != finalResultAt

        if (!basicInfoChanged && !questionsChanged) {
            return
        }

        val admin = applyUserSyncService.getOrSync(adminEmail)

        saveModifyLog(
            recruit = recruit,
            admin = admin,
            field = "title",
            fieldLabel = "제목 수정",
            before = recruit.title,
            after = title
        )

        saveModifyLog(
            recruit = recruit,
            admin = admin,
            field = "generation",
            fieldLabel = "기수 수정",
            before = recruit.generation.toString(),
            after = request.generation.toString()
        )

        saveModifyLog(
            recruit = recruit,
            admin = admin,
            field = "startAt",
            fieldLabel = "모집 시작일 수정",
            before = recruit.startAt.toString(),
            after = startAt.toString()
        )

        saveModifyLog(
            recruit = recruit,
            admin = admin,
            field = "endAt",
            fieldLabel = "모집 종료일 수정",
            before = recruit.endAt.toString(),
            after = endAt.toString()
        )

        saveModifyLog(
            recruit = recruit,
            admin = admin,
            field = "documentResultAt",
            fieldLabel = "1차 결과 발표일 수정",
            before = recruit.documentResultAt.toString(),
            after = documentResultAt.toString()
        )

        saveModifyLog(
            recruit = recruit,
            admin = admin,
            field = "finalResultAt",
            fieldLabel = "최종 결과 발표일 수정",
            before = recruit.finalResultAt.toString(),
            after = finalResultAt.toString()
        )

        if (questionsChanged) {
            saveModifyLog(
                recruit = recruit,
                admin = admin,
                field = "questions",
                fieldLabel = "지원서 질문 수정",
                before = "${previousQuestions.size}개",
                after = "${request.questions.size}개"
            )
        }

        recruit.update(
            title = title,
            generation = request.generation,
            startAt = startAt,
            endAt = endAt,
            documentResultAt = documentResultAt,
            finalResultAt = finalResultAt
        )

        // 질문이 실제로 바뀌었고 지원서가 없을 때만 교체한다.
        if (questionsChanged) {
            recruitContentRepository.deleteAllByRecruitId(recruitId)

            recruitContentRepository.saveAll(
                createRecruitContents(recruit, request)
            )
        }
    }

    @Transactional
    fun deleteRecruit(recruitId: Long) {
        val recruit = findActiveRecruit(recruitId)

        if (applicationRepository.existsByRecruitId(recruitId)) {
            throw GlobalException(ErrorCode.CONFLICT)
        }

        recruit.softDelete()
    }

    private fun saveModifyLog(
        recruit: Recruit,
        admin: ApplyUser,
        field: String,
        fieldLabel: String,
        before: String?,
        after: String?
    ) {
        if (before == after) {
            return
        }

        applyModifyLogRepository.save(
            ApplyModifyLog(
                field = field,
                fieldLabel = fieldLabel,
                beforeValue = before,
                afterValue = after,
                recruit = recruit,
                application = null,
                user = admin
            )
        )
    }

    private fun questionFingerprint(
        content: RecruitContent
    ): List<String> {
        return listOf(
            content.question.trim(),
            content.priority.toString(),
            content.required.toString(),
            content.minLength.toString(),
            content.maxLength.toString()
        )
    }

    private fun questionFingerprint(
        question: AdminRecruitCreateRequest.Question
    ): List<String> {
        return listOf(
            question.question.trim(),
            question.priority.toString(),
            question.required.toString(),
            question.minLength.toString(),
            question.maxLength.toString()
        )
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

    private fun findActiveRecruit(
        recruitId: Long
    ): Recruit {
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
        val expectedPriorities =
            (1..request.questions.size).toList()

        if (priorities.sorted() != expectedPriorities) {
            throw invalidRequest()
        }

        request.questions.forEach { question ->
            val minLength = question.minLength
                ?: throw invalidRequest()
            val maxLength = question.maxLength
                ?: throw invalidRequest()

            if (minLength > maxLength) {
                throw invalidRequest()
            }
        }
    }

    private fun invalidRequest(): GlobalException {
        return GlobalException(ErrorCode.INVALID_REQUEST)
    }
}