package com.likelionknu.applyserver.user.service

import com.likelionknu.applyserver.auth.client.AuthUserClient
import com.likelionknu.applyserver.user.data.dto.response.ApplyUserProfileResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ApplyUserService(
    private val authUserClient: AuthUserClient
) {
    private val log = LoggerFactory.getLogger(ApplyUserService::class.java)

    fun getMyProfile(email: String): ApplyUserProfileResponse {
        val authUserResponse = authUserClient.getUser(email)

        log.info("[getMyProfile] 사용자 프로필 조회: {}", email)

        return ApplyUserProfileResponse.from(authUserResponse)
    }
}