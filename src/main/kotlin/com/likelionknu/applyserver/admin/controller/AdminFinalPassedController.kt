package com.likelionknu.applyserver.admin.controller

import com.likelionknu.applyserver.admin.data.dto.response.AdminFinalPassedResponse
import com.likelionknu.applyserver.admin.service.AdminFinalPassedService
import com.likelionknu.applyserver.common.response.GlobalResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/recruits")
class AdminFinalPassedController(
    private val adminFinalPassedService: AdminFinalPassedService
) {

    @GetMapping("/{recruitId}/final-passed")
    @Operation(summary = "관리자 최종 합격자 명단 조회")
    fun getFinalPassedApplicants(
        @PathVariable recruitId: Long
    ): GlobalResponse<List<AdminFinalPassedResponse>> {
        return GlobalResponse.ok(
            adminFinalPassedService.getFinalPassedApplicants(recruitId)
        )
    }
}