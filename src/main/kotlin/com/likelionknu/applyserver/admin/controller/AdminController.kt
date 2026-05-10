package com.likelionknu.applyserver.admin.controller

import com.likelionknu.applyserver.admin.data.dto.request.AdminMemoRequestDto
import com.likelionknu.applyserver.admin.data.dto.request.AdminUserRoleUpdateRequestDto
import com.likelionknu.applyserver.admin.data.dto.request.ApplicationStatusUpdateRequestDto
import com.likelionknu.applyserver.admin.data.dto.response.AdminUserDetailResponseDto
import com.likelionknu.applyserver.admin.data.dto.response.AdminUserResponseDto
import com.likelionknu.applyserver.admin.service.AdminApplicationService
import com.likelionknu.applyserver.admin.service.AdminUserService
import com.likelionknu.applyserver.application.data.dto.response.ApplicationInfoResponseDto
import com.likelionknu.applyserver.application.service.ApplicationMailService
import com.likelionknu.applyserver.application.service.ApplicationService
import com.likelionknu.applyserver.common.response.GlobalResponse
import com.likelionknu.applyserver.common.security.SecurityUtil
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin")
class AdminController(
    private val applicationService: ApplicationService,
    private val adminUserService: AdminUserService,
    private val applicationMailService: ApplicationMailService,
    private val adminApplicationService: AdminApplicationService
) {

    @GetMapping("/ping")
    fun ping(): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

    @GetMapping("/applications/{id}")
    @Operation(summary = "지원서 상세 정보 조회")
    fun getApplicationInfo(@PathVariable id: Long): GlobalResponse<ApplicationInfoResponseDto> {
        return GlobalResponse.ok(applicationService.getApplicationInfo(id))
    }

    @GetMapping("/users")
    @Operation(summary = "모든 사용자 목록 조회")
    fun getAllUsers(): GlobalResponse<List<AdminUserResponseDto>> {
        return GlobalResponse.ok(adminUserService.getAllUsers())
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "특정 사용자 상세 정보 조회")
    fun getUserDetail(@PathVariable id: Long): GlobalResponse<AdminUserDetailResponseDto> {
        return GlobalResponse.ok(adminUserService.getUserDetail(id))
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "특정 사용자 강제 회원탈퇴")
    fun deleteUser(@PathVariable id: Long): GlobalResponse<Void> {
        adminUserService.deleteUser(id)
        return GlobalResponse.ok()
    }

    @PatchMapping("/users/{id}/role")
    @Operation(summary = "특정 사용자 권한 변경")
    fun updateUserRole(
        @PathVariable id: Long,
        @RequestBody request: AdminUserRoleUpdateRequestDto
    ): GlobalResponse<Void> {
        adminUserService.updateUserRole(id, request.role)
        return GlobalResponse.ok()
    }

    @GetMapping("/recruits/{id}/notifications/document")
    @Operation(summary = "서류 합격 안내 메일 발송")
    fun sendDocumentResult(@PathVariable id: Long): GlobalResponse<Void> {
        applicationMailService.sendDocumentResult(SecurityUtil.getUsername(), id)
        return GlobalResponse.ok()
    }

    @GetMapping("/recruits/{id}/notifications/final")
    @Operation(summary = "최종 합격 안내 메일 발송")
    fun sendFinalResult(@PathVariable id: Long): GlobalResponse<Void> {
        applicationMailService.sendFinalResult(SecurityUtil.getUsername(), id)
        return GlobalResponse.ok()
    }

    @PostMapping("/applications/{id}/memos")
    @Operation(summary = "운영진 메모 등록")
    fun sendAdminMemo(
        @PathVariable id: Long,
        @Valid @RequestBody request: AdminMemoRequestDto
    ): GlobalResponse<Void> {
        adminApplicationService.saveAdminMemo(id, request)
        return GlobalResponse.ok()
    }

    @PatchMapping("/applications/{id}")
    @Operation(summary = "특정 지원서 상태 & 평가 변경")
    fun updateApplicationStatus(
        @PathVariable id: Long,
        @RequestBody request: ApplicationStatusUpdateRequestDto
    ): GlobalResponse<Void> {
        adminApplicationService.patch(id, request)
        return GlobalResponse.ok()
    }

    @DeleteMapping("/applications/{id}")
    @Operation(summary = "특정 지원서 강제 삭제")
    fun deleteAdminApplication(@PathVariable id: Long): GlobalResponse<Void> {
        adminApplicationService.deleteAdminApplication(id)
        return GlobalResponse.ok()
    }
}