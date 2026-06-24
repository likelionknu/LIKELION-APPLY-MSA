package com.likelionknu.applyserver.common.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class HeaderAuthenticationFilter : OncePerRequestFilter() {

    companion object {
        private const val USER_EMAIL_HEADER = "X-User-Email"
        private const val USER_ROLE_HEADER = "X-User-Role"
        private const val DEFAULT_ROLE = "MEMBER"
        private const val ROLE_PREFIX = "ROLE_"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val email = request.getHeader(USER_EMAIL_HEADER)

        if (
            !email.isNullOrBlank() &&
            SecurityContextHolder.getContext().authentication == null
        ) {
            val role = request.getHeader(USER_ROLE_HEADER)
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: DEFAULT_ROLE

            val authority = if (role.startsWith(ROLE_PREFIX)) {
                role
            } else {
                ROLE_PREFIX + role
            }

            val authentication =
                UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    listOf(SimpleGrantedAuthority(authority))
                )

            SecurityContextHolder.getContext().authentication =
                authentication
        }

        filterChain.doFilter(request, response)
    }
}