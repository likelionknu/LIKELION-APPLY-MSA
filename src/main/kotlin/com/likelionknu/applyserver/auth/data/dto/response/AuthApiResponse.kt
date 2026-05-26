package com.likelionknu.applyserver.auth.data.dto.response

data class AuthApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)