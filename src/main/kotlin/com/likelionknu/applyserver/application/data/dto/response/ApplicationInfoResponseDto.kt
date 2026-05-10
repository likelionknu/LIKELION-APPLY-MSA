package com.likelionknu.applyserver.application.data.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class ApplicationInfoResponseDto (
    val name: String,
    val email: String,
    val depart: String? = null,

    @field:JsonProperty("student_id")
    val studentId: String? = null,

    val grade: Int? = null,

    @field:JsonProperty("student_status")
    val studentStatus: String? = null,

    val status: String,

    @field:JsonProperty("submitted_at")
    val submittedAt: LocalDateTime,

    val phone: String? = null,
    val answers: List<ApplicationAnswerResponseDto>
)