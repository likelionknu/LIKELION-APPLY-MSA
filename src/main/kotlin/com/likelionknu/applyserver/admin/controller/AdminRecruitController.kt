package com.likelionknu.applyserver.admin.controller

import com.likelionknu.applyserver.admin.data.dto.request.AdminRecruitUpdateRequestDto
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitApplicationResponseDto
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitDetailResponseDto
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitSummaryResponseDto
import com.likelionknu.applyserver.admin.service.AdminRecruitApplicationService
import com.likelionknu.applyserver.admin.service.AdminRecruitService
import com.likelionknu.applyserver.common.response.GlobalResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin/recruits")
class AdminRecruitController(
    private val adminRecruitService: AdminRecruitService,
    private val adminRecruitApplicationService: AdminRecruitApplicationService
) {

    @PostMapping
    @Operation(summary = "모집 공고 등록")
    fun createRecruit(
        @RequestBody request: AdminRecruitUpdateRequestDto
    ): GlobalResponse<Void> {
        adminRecruitService.createRecruit(request)
        return GlobalResponse.ok()
    }

    @GetMapping("/{id}")
    @Operation(summary = "모집 공고 상세 정보 조회")
    fun getRecruitDetail(
        @PathVariable id: Long
    ): GlobalResponse<AdminRecruitDetailResponseDto> {
        return GlobalResponse.ok(adminRecruitService.getRecruitDetail(id))
    }

    @GetMapping
    @Operation(summary = "모집 공고 현황 조회")
    fun getRecruitSummaries(): GlobalResponse<List<AdminRecruitSummaryResponseDto>> {
        return GlobalResponse.ok(adminRecruitService.getRecruitSummaries())
    }

    @GetMapping("/{id}/applications")
    @Operation(summary = "모집 공고에 등록된 전체 지원서 조회")
    fun getRecruitApplications(
        @PathVariable id: Long
    ): GlobalResponse<List<AdminRecruitApplicationResponseDto>> {
        return GlobalResponse.ok(adminRecruitApplicationService.getApplicationsByRecruit(id))
    }

    @PutMapping("/{id}")
    @Operation(summary = "특정 모집 공고 수정")
    fun updateRecruit(
        @PathVariable id: Long,
        @RequestBody request: AdminRecruitUpdateRequestDto
    ): GlobalResponse<Void> {
        adminRecruitService.updateRecruit(id, request)
        return GlobalResponse.ok()
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "특정 모집 공고 삭제")
    fun deleteRecruit(
        @PathVariable id: Long
    ): GlobalResponse<Void> {
        adminRecruitService.deleteRecruit(id)
        return GlobalResponse.ok()
    }
}