package com.likelionknu.applyserver.application.controller

import com.likelionknu.applyserver.application.data.dto.request.ApplicationDraftSaveRequest
import com.likelionknu.applyserver.application.data.dto.request.FinalSubmitRequestDto
import com.likelionknu.applyserver.application.data.dto.response.ApplicationDetailResponse
import com.likelionknu.applyserver.application.data.dto.response.ApplicationSummaryResponse
import com.likelionknu.applyserver.application.service.ApplicationCancelService
import com.likelionknu.applyserver.application.service.ApplicationFinalSubmitService
import com.likelionknu.applyserver.application.service.ApplicationQueryService
import com.likelionknu.applyserver.application.service.ApplicationService
import com.likelionknu.applyserver.auth.data.entity.User
import com.likelionknu.applyserver.auth.data.repository.UserRepository
import com.likelionknu.applyserver.common.response.GlobalResponse
import com.likelionknu.applyserver.common.security.SecurityUtil
import com.likelionknu.applyserver.common.security.exception.AuthenticationInfoException
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/applications")
class ApplicationController(
    private val applicationService: ApplicationService,
    private val userRepository: UserRepository,
    private val applicationFinalSubmitService: ApplicationFinalSubmitService,
    private val applicationQueryService: ApplicationQueryService,
    private val applicationCancelService: ApplicationCancelService
) {
    @PostMapping
    @Operation(summary = "지원서 최종 제출")
    fun finalSubmit(
        @RequestBody request: @Valid FinalSubmitRequestDto
    ): GlobalResponse<Void> {
        applicationFinalSubmitService.finalSubmit(SecurityUtil.getUsername(), request)
        return GlobalResponse.ok()
    }

    @PutMapping("/drafts/{recruitId}")
    @Operation(summary = "지원서 임시 저장 (DRAFT 상태에서만 가능)")
    fun saveDraft(
        @PathVariable("recruitId") recruitId: Long,
        @RequestBody requests: List<ApplicationDraftSaveRequest>
    ): GlobalResponse<Long> {
        val email = SecurityUtil.getUsername()
        if (email.isBlank()) {
            throw AuthenticationInfoException()
        }

        val user: User = userRepository.findByEmail(email)

        val applicationId = applicationService.saveDraft(user.id!!, recruitId, requests)
        return GlobalResponse.ok(applicationId)
    }

    @GetMapping
    @Operation(summary = "내 지원서 목록 조회")
    fun getMyApplications(): GlobalResponse<List<ApplicationSummaryResponse?>?> {
        return GlobalResponse.ok(applicationQueryService.getMyApplications(SecurityUtil.getUsername()))
    }

    @GetMapping("/{id}")
    @Operation(summary = "지원서 상세 조회")
    fun getApplicationDetail(
        @PathVariable id: Long
    ): GlobalResponse<ApplicationDetailResponse?> {
        return GlobalResponse.ok(
            applicationQueryService.getApplicationDetail(SecurityUtil.getUsername(), id))
    }

    @PostMapping("/{recruitId}/cancel")
    @Operation(summary = "지원서 회수(지원 취소) - 모든 상태 → CANCELED")
    fun cancelApplication(
        @PathVariable recruitId: Long
    ): GlobalResponse<Void> {
        val user: User = userRepository.findByEmail(SecurityUtil.getUsername())
        applicationCancelService.cancel(user.id!!, recruitId)
        return GlobalResponse.ok()
    }

    @PostMapping("/{recruitId}/restore")
    @Operation(summary = "지원서 회수 취소(상태 복원) - CANCELED → beforeCanceledStatus")
    fun restoreApplication(
        @PathVariable recruitId: Long
    ): GlobalResponse<Void> {
        val user: User = userRepository.findByEmail(SecurityUtil.getUsername())
        applicationCancelService.restore(user.id!!, recruitId)
        return GlobalResponse.ok()
    }
}