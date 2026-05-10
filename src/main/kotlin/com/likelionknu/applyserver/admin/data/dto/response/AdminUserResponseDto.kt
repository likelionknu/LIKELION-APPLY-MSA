package com.likelionknu.applyserver.admin.data.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class AdminUserResponseDto(
    @field:JsonProperty("user_id")
    val userId: Long,

    val name: String,

    val email: String,

    val role: String,

    val depart: String?,

    @field:JsonProperty("created_at")
    val createdAt: LocalDateTime?,

    @field:JsonProperty("last_accessed_at")
    val lastAccessedAt: LocalDateTime?
)