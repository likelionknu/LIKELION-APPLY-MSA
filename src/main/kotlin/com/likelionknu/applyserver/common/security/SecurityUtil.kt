package com.likelionknu.applyserver.common.security

import com.likelionknu.applyserver.common.security.exception.AuthenticationInfoException
import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtil {
    @JvmStatic
    fun getUsername(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication?.name

        if (username.isNullOrBlank()) {
            throw AuthenticationInfoException()
        }

        return username
    }
}