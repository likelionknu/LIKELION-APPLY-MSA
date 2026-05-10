package com.likelionknu.applyserver.common.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.contentType = "application/json;charset=UTF-8"
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val errorResponse: GlobalResponse<Nothing> = GlobalResponse.error(ErrorCode.UNAUTHORIZED)
        val objectMapper = ObjectMapper()
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}