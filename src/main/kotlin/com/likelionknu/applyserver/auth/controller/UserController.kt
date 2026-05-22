package com.likelionknu.applyserver.auth.controller

import com.likelionknu.applyserver.auth.data.dto.request.ModifyProfileRequestDto
import com.likelionknu.applyserver.auth.data.dto.response.ProfileResponseDto
import com.likelionknu.applyserver.auth.service.UserService
import com.likelionknu.applyserver.common.response.GlobalResponse
import com.likelionknu.applyserver.common.security.SecurityUtil
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {
    @PatchMapping("/me/profile")
    @Operation(summary = "사용자 프로필 등록(변경)")
    fun modifyUsersProfile(
        @Valid @RequestBody modifyProfileRequestDto: ModifyProfileRequestDto
    ): GlobalResponse<ProfileResponseDto> {
        val profileResponse = userService.modifyUsersProfile(
            SecurityUtil.getUsername(),
            modifyProfileRequestDto
        )
        return GlobalResponse.ok(profileResponse)
    }

    @GetMapping("/me/profile")
    @Operation(summary = "사용자 프로필 조회")
    fun getUsersProfile(): GlobalResponse<ProfileResponseDto> {
        return GlobalResponse.ok(userService.getUsersProfile(SecurityUtil.getUsername()))
    }
}