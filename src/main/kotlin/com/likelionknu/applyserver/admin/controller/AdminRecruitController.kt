package com.likelionknu.applyserver.admin.controller

import com.likelionknu.applyserver.admin.data.dto.request.AdminRecruitCreateRequest
import com.likelionknu.applyserver.admin.data.dto.response.AdminModifyLogResponse
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitDetailResponse
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitListResponse
import com.likelionknu.applyserver.admin.service.AdminRecruitService
import com.likelionknu.applyserver.common.response.GlobalResponse
import com.likelionknu.applyserver.common.security.SecurityUtil
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/recruits")
class AdminRecruitController(
    private val adminRecruitService: AdminRecruitService
) {

    @GetMapping
    @Operation(summary = "관리자 전체 공고 조회")
    fun getRecruits():
            GlobalResponse<List<AdminRecruitListResponse>> {
        return GlobalResponse.ok(
            adminRecruitService.getRecruits()
        )
    }

    @GetMapping("/{recruitId}")
    @Operation(summary = "관리자 특정 공고 상세 조회")
    fun getRecruit(
        @PathVariable recruitId: Long
    ): GlobalResponse<AdminRecruitDetailResponse> {
        return GlobalResponse.ok(
            adminRecruitService.getRecruit(recruitId)
        )
    }

    @GetMapping("/{recruitId}/log")
    @Operation(summary = "관리자 특정 공고 변경 이력 조회")
    fun getModifyLogs(
        @PathVariable recruitId: Long
    ): GlobalResponse<List<AdminModifyLogResponse>> {
        return GlobalResponse.ok(
            adminRecruitService.getModifyLogs(recruitId)
        )
    }

    @PostMapping
    @Operation(summary = "관리자 새 공고 등록")
    fun createRecruit(
        @RequestBody @Valid request: AdminRecruitCreateRequest
    ): GlobalResponse<Long> {
        return GlobalResponse.ok(
            adminRecruitService.createRecruit(request)
        )
    }

    @PutMapping("/{recruitId}")
    @Operation(summary = "관리자 특정 공고 수정")
    fun updateRecruit(
        @PathVariable recruitId: Long,
        @RequestBody @Valid request: AdminRecruitCreateRequest
    ): GlobalResponse<Void> {
        adminRecruitService.updateRecruit(
            recruitId = recruitId,
            adminEmail = SecurityUtil.getUsername(),
            request = request
        )

        return GlobalResponse.ok()
    }

    @DeleteMapping("/{recruitId}")
    @Operation(summary = "관리자 특정 공고 삭제")
    fun deleteRecruit(
        @PathVariable recruitId: Long
    ): GlobalResponse<Void> {
        adminRecruitService.deleteRecruit(recruitId)
        return GlobalResponse.ok()
    }
}