package com.likelionknu.applyserver.application.controller

import com.likelionknu.applyserver.application.data.dto.request.ApplicationDraftSaveRequest
import com.likelionknu.applyserver.application.data.dto.request.FinalSubmitRequestDto
import com.likelionknu.applyserver.application.data.dto.response.ApplicationDetailResponse
import com.likelionknu.applyserver.application.data.dto.response.ApplicationSummaryResponse
import com.likelionknu.applyserver.application.service.ApplicationCancelService
import com.likelionknu.applyserver.application.service.ApplicationFinalSubmitService
import com.likelionknu.applyserver.application.service.ApplicationQueryService
import com.likelionknu.applyserver.application.service.ApplicationService
import com.likelionknu.applyserver.common.response.GlobalResponse
import com.likelionknu.applyserver.common.security.SecurityUtil
import com.likelionknu.applyserver.user.service.ApplyUserSyncService
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/applications")
class ApplicationController(
    private val applicationService: ApplicationService,
    private val applicationFinalSubmitService: ApplicationFinalSubmitService,
    private val applicationQueryService: ApplicationQueryService,
    private val applicationCancelService: ApplicationCancelService,
    private val applyUserSyncService: ApplyUserSyncService
) {

    @PostMapping
    @Operation(summary = "지원서 최종 제출")
    fun finalSubmit(
        @RequestBody request: @Valid FinalSubmitRequestDto
    ): GlobalResponse<Void> {
        applicationFinalSubmitService.finalSubmit(
            SecurityUtil.getUsername(),
            request
        )
        return GlobalResponse.ok()
    }

    @PutMapping("/drafts/{recruitId}")
    @Operation(summary = "지원서 임시 저장 (DRAFT 상태에서만 가능)")
    fun saveDraft(
        @PathVariable recruitId: Long,
        @RequestBody @Valid request: ApplicationDraftSaveRequest
    ): GlobalResponse<Long> {
        val applyUser = applyUserSyncService.getOrSync(
            SecurityUtil.getUsername()
        )

        val applicationId = applicationService.saveDraft(
            userId = applyUser.id!!,
            recruitId = recruitId,
            request = request
        )

        return GlobalResponse.ok(applicationId)
    }

    @GetMapping
    @Operation(summary = "내 지원서 목록 조회")
    fun getMyApplications(): GlobalResponse<List<ApplicationSummaryResponse>> {
        return GlobalResponse.ok(
            applicationQueryService.getMyApplications(
                SecurityUtil.getUsername()
            )
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "지원서 상세 조회")
    fun getApplicationDetail(
        @PathVariable id: Long
    ): GlobalResponse<ApplicationDetailResponse> {
        return GlobalResponse.ok(
            applicationQueryService.getApplicationDetail(
                SecurityUtil.getUsername(),
                id
            )
        )
    }

    @PostMapping("/{recruitId}/cancel")
    @Operation(summary = "지원서 회수(지원 취소) - 모든 상태 -> CANCELED")
    fun cancelApplication(
        @PathVariable recruitId: Long
    ): GlobalResponse<Void> {
        val applyUser = applyUserSyncService.getOrSync(
            SecurityUtil.getUsername()
        )

        applicationCancelService.cancel(
            applyUser.id!!,
            recruitId
        )

        return GlobalResponse.ok()
    }

    @PostMapping("/{recruitId}/restore")
    @Operation(summary = "지원서 회수 취소(상태 복원) - CANCELED -> beforeCanceledStatus")
    fun restoreApplication(
        @PathVariable recruitId: Long
    ): GlobalResponse<Void> {
        val applyUser = applyUserSyncService.getOrSync(
            SecurityUtil.getUsername()
        )

        applicationCancelService.restore(
            applyUser.id!!,
            recruitId
        )

        return GlobalResponse.ok()
    }
}