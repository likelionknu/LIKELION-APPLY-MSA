package com.likelionknu.applyserver.admin.service

import com.likelionknu.applyserver.admin.data.dto.request.AdminApplicationEvaluationRequest
import com.likelionknu.applyserver.admin.data.dto.request.AdminApplicationStatusRequest
import com.likelionknu.applyserver.admin.data.dto.response.AdminApplicationResponse
import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.application.data.entity.ApplyModifyLog
import com.likelionknu.applyserver.application.data.exception.ApplicationNotFoundException
import com.likelionknu.applyserver.application.data.exception.RecruitNotFoundException
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.application.data.repository.ApplyModifyLogRepository
import com.likelionknu.applyserver.auth.data.enums.ApplicationEvaluation
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import com.likelionknu.applyserver.user.service.ApplyUserSyncService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminApplicationService(
    private val applicationRepository: ApplicationRepository,
    private val applyModifyLogRepository: ApplyModifyLogRepository,
    private val recruitRepository: RecruitRepository,
    private val applyUserSyncService: ApplyUserSyncService
) {

    @Transactional(readOnly = true)
    fun getApplications(
        recruitId: Long
    ): List<AdminApplicationResponse> {
        val recruit = recruitRepository.findById(recruitId)
            .orElseThrow { RecruitNotFoundException() }

        if (recruit.deletedAt != null) {
            throw RecruitNotFoundException()
        }

        return applicationRepository
            .findAllWithUserAndAnswersByRecruitId(recruitId)
            .map(AdminApplicationResponse::from)
    }

    @Transactional
    fun updateEvaluation(
        applicationId: Long,
        adminEmail: String,
        request: AdminApplicationEvaluationRequest
    ) {
        val application = findApplication(applicationId)
        val target = findEvaluationTarget(request)

        val previous = getCurrentEvaluation(
            application,
            target.field
        )

        val next = parseEvaluation(target.value)

        if (previous == next) {
            return
        }

        updateApplicationEvaluation(
            application,
            target.field,
            next
        )

        saveModifyLog(
            application = application,
            adminEmail = adminEmail,
            field = target.field.fieldName,
            fieldLabel = target.field.label,
            before = evaluationValue(previous),
            after = evaluationValue(next)
        )
    }

    @Transactional
    fun updateStatus(
        applicationId: Long,
        adminEmail: String,
        request: AdminApplicationStatusRequest
    ) {
        val application = findApplication(applicationId)
        val previous = application.status
        val next = parseStatus(request.status)

        if (previous == next) {
            return
        }

        application.changeStatus(next)

        saveModifyLog(
            application = application,
            adminEmail = adminEmail,
            field = "status",
            fieldLabel = "제출 상태 변경",
            before = previous.name.lowercase(),
            after = next.name.lowercase()
        )
    }

    private fun findApplication(
        applicationId: Long
    ): Application {
        return applicationRepository.findById(applicationId)
            .orElseThrow { ApplicationNotFoundException() }
    }

    private fun saveModifyLog(
        application: Application,
        adminEmail: String,
        field: String,
        fieldLabel: String,
        before: String?,
        after: String?
    ) {
        val admin = applyUserSyncService.getOrSync(adminEmail)

        applyModifyLogRepository.save(
            ApplyModifyLog(
                field = field,
                fieldLabel = fieldLabel,
                beforeValue = before,
                afterValue = after,
                recruit = application.recruit,
                application = application,
                user = admin
            )
        )
    }

    private fun findEvaluationTarget(
        request: AdminApplicationEvaluationRequest
    ): EvaluationTarget {
        val targets = listOfNotNull(
            request.documentEvaluation?.let {
                EvaluationTarget(EvaluationField.DOCUMENT, it)
            },
            request.interviewEvaluation?.let {
                EvaluationTarget(EvaluationField.INTERVIEW, it)
            },
            request.evaluation?.let {
                EvaluationTarget(EvaluationField.FINAL, it)
            }
        )

        if (targets.size != 1) {
            throw GlobalException(ErrorCode.INVALID_REQUEST)
        }

        return targets.first()
    }

    private fun parseEvaluation(
        value: String
    ): ApplicationEvaluation? {
        return when (value.trim().uppercase()) {
            "NOT_REVIEWED" -> null
            "PASS" -> ApplicationEvaluation.PASS
            "FAIL" -> ApplicationEvaluation.FAIL
            "HOLD" -> ApplicationEvaluation.HOLD
            else -> throw GlobalException(ErrorCode.INVALID_REQUEST)
        }
    }

    private fun parseStatus(
        value: String
    ): ApplicationStatus {
        return when (value.trim().uppercase()) {
            "DRAFT" -> ApplicationStatus.DRAFT
            "SUBMITTED" -> ApplicationStatus.SUBMITTED
            "CANCELED" -> ApplicationStatus.CANCELED
            else -> throw GlobalException(ErrorCode.INVALID_REQUEST)
        }
    }

    private fun getCurrentEvaluation(
        application: Application,
        field: EvaluationField
    ): ApplicationEvaluation? {
        return when (field) {
            EvaluationField.DOCUMENT ->
                application.documentEvaluation

            EvaluationField.INTERVIEW ->
                application.interviewEvaluation

            EvaluationField.FINAL ->
                application.evaluation
        }
    }

    private fun updateApplicationEvaluation(
        application: Application,
        field: EvaluationField,
        evaluation: ApplicationEvaluation?
    ) {
        when (field) {
            EvaluationField.DOCUMENT ->
                application.updateDocumentEvaluation(evaluation)

            EvaluationField.INTERVIEW ->
                application.updateInterviewEvaluation(evaluation)

            EvaluationField.FINAL ->
                application.updateEvaluation(evaluation)
        }
    }

    private fun evaluationValue(
        evaluation: ApplicationEvaluation?
    ): String {
        return evaluation?.name?.lowercase() ?: "not_reviewed"
    }

    private data class EvaluationTarget(
        val field: EvaluationField,
        val value: String
    )

    private enum class EvaluationField(
        val fieldName: String,
        val label: String
    ) {
        DOCUMENT(
            fieldName = "documentEvaluation",
            label = "1차 서류 평가"
        ),
        INTERVIEW(
            fieldName = "interviewEvaluation",
            label = "2차 면접 평가"
        ),
        FINAL(
            fieldName = "evaluation",
            label = "최종 합격 평가"
        )
    }
}