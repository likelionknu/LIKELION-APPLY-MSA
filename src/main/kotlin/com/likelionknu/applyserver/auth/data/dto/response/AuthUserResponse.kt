package com.likelionknu.applyserver.auth.data.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class AuthUserResponse(
    val email: String,
    val name: String,

    @JsonProperty("profile_url")
    val profileUrl: String? = null,

    val role: String,

    val depart: String? = null,
    val grade: Int? = null,
    val phone: String? = null,

    @JsonProperty("student_id")
    val studentId: String? = null,

    @JsonProperty("academic_status")
    val academicStatus: String? = null,

    val part: String? = null,
    val course: Int? = null,

    @JsonProperty("joined_at")
    val joinedAt: LocalDateTime? = null
)