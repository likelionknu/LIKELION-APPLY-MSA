package com.likelionknu.applyserver.admin.controller

import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitListResponse
import com.likelionknu.applyserver.admin.service.AdminRecruitService
import com.likelionknu.applyserver.common.response.GlobalResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/recruits")
class AdminRecruitController(
    private val adminRecruitService: AdminRecruitService
) {

    @GetMapping
    @Operation(summary = "관리자 전체 공고 조회")
    fun getRecruits(): GlobalResponse<List<AdminRecruitListResponse>> {
        return GlobalResponse.ok(adminRecruitService.getRecruits())
    }
}