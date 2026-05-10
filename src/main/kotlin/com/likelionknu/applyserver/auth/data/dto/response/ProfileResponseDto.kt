package com.likelionknu.applyserver.auth.data.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class ProfileResponseDto(
    val email: String,
    val name: String,

    @JsonProperty("profile_url")
    val profileUrl: String,

    val depart: String?,

    @JsonProperty("student_id")
    val studentId: String?,

    val grade: Int?,
    val phone: String?,
    val status: String?
)