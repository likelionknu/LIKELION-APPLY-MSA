package com.likelionknu.applyserver.common.security

data class AuthenticationToken(
    val accessToken: String,
    val refreshToken: String
)