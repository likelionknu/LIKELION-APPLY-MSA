package com.likelionknu.applyserver.auth.client

import com.likelionknu.applyserver.auth.data.dto.response.AuthUserResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "authUserClient",
    url = "\${services.auth.url:http://localhost:8081}"
)
interface AuthUserClient {

    @GetMapping("/auth/v1/users")
    fun getUser(
        @RequestHeader("X-User-Email") email: String
    ): AuthUserResponse
}