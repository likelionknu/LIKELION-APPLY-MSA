package com.likelionknu.applyserver.admin.controller

import com.likelionknu.applyserver.admin.data.dto.response.AdminApplicationResponse
import com.likelionknu.applyserver.admin.service.AdminApplicationService
import com.likelionknu.applyserver.common.response.GlobalResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
}