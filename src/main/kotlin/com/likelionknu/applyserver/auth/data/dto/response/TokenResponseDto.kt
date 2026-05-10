package com.likelionknu.applyserver.auth.data.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenResponseDto(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("refresh_token")
    val refreshToken: String,

    @JsonProperty("is_new_user")
    val isNewUser: Boolean = false,

    val name: String,
    val role: String
)