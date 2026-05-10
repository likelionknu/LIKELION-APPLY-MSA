package com.likelionknu.applyserver.auth.controller

import com.likelionknu.applyserver.auth.data.dto.response.TokenResponseDto
import com.likelionknu.applyserver.auth.data.enums.PlatformDivider
import com.likelionknu.applyserver.auth.service.AuthService
import com.likelionknu.applyserver.common.response.GlobalResponse
import com.likelionknu.applyserver.common.security.SecurityUtil
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
        private val authService: AuthService
){
    @PostMapping("/login")
    @Operation(summary = "구글 소셜 통합 로그인")
    fun login( @RequestParam code: String,
               @RequestParam(required = false, defaultValue = "APPLY") platform: PlatformDivider):
            GlobalResponse<TokenResponseDto>{
        val tokenResponse = authService.userSocialSignIn(code, platform)
        return GlobalResponse.ok(tokenResponse)
    }

    @PostMapping("/reissue")
    @Operation(summary = "Access Token 재발급")
    fun reissue(@RequestParam(name = "refresh_token") refreshToken: String):
            GlobalResponse<TokenResponseDto>{
        return GlobalResponse.ok(authService.reissue(refreshToken))
    }

    @PostMapping("/logout")
    @Operation(summary = "사용자 로그아웃")
    fun logout(): GlobalResponse<Void> {
        authService.userLogout(SecurityUtil.getUsername())
        return GlobalResponse.ok()
    }
}
