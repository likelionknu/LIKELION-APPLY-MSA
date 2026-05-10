package com.likelionknu.applyserver.admin.data.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class AdminUserDetailResponseDto(
    val name: String,

    val email: String,

    val phone: String?,

    @field:JsonProperty("student_id")
    val studentId: String?,

    val depart: String?,

    val grade: Int?,

    val status: String?,

    val role: String
)