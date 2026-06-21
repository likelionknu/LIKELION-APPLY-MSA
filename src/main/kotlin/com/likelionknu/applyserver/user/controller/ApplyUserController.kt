package com.likelionknu.applyserver.user.controller

import com.likelionknu.applyserver.common.response.GlobalResponse
import com.likelionknu.applyserver.common.security.SecurityUtil
import com.likelionknu.applyserver.user.data.dto.response.ApplyUserProfileResponse
import com.likelionknu.applyserver.user.service.ApplyUserService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/apply/v1/users")
class ApplyUserController(
    private val applyUserService: ApplyUserService
) {

    @GetMapping("/me/profile")
    @Operation(summary = "사용자 프로필 조회")
    fun getMyProfile(): GlobalResponse<ApplyUserProfileResponse> {
        return GlobalResponse.ok(
            applyUserService.getMyProfile(SecurityUtil.getUsername())
        )
    }
}