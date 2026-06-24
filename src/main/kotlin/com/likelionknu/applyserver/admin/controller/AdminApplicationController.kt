package com.likelionknu.applyserver.admin.controller

import com.likelionknu.applyserver.admin.data.dto.request.AdminApplicationEvaluationRequest
import com.likelionknu.applyserver.admin.data.dto.request.AdminApplicationStatusRequest
import com.likelionknu.applyserver.admin.data.dto.response.AdminApplicationResponse
import com.likelionknu.applyserver.admin.service.AdminApplicationService
import com.likelionknu.applyserver.common.response.GlobalResponse
import com.likelionknu.applyserver.common.security.SecurityUtil
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/applications")
class AdminApplicationController(
    private val adminApplicationService: AdminApplicationService
) {

    @GetMapping("/{recruitId}")
    @Operation(summary = "관리자 특정 공고 지원서 조회")
    fun getApplications(
        @PathVariable recruitId: Long
    ): GlobalResponse<List<AdminApplicationResponse>> {
        return GlobalResponse.ok(
            adminApplicationService.getApplications(recruitId)
        )
    }

    @PutMapping("/{applicationId}/evaluations")
    @Operation(summary = "관리자 특정 지원서 평가 상태 변경")
    fun updateEvaluation(
        @PathVariable applicationId: Long,
        @RequestBody request: AdminApplicationEvaluationRequest
    ): GlobalResponse<Void> {
        adminApplicationService.updateEvaluation(
            applicationId = applicationId,
            adminEmail = SecurityUtil.getUsername(),
            request = request
        )

        return GlobalResponse.ok()
    }

    @PutMapping("/{applicationId}/status")
    @Operation(summary = "관리자 특정 지원서 제출 상태 변경")
    fun updateStatus(
        @PathVariable applicationId: Long,
        @RequestBody @Valid request: AdminApplicationStatusRequest
    ): GlobalResponse<Void> {
        adminApplicationService.updateStatus(
            applicationId = applicationId,
            adminEmail = SecurityUtil.getUsername(),
            request = request
        )

        return GlobalResponse.ok()
    }
}