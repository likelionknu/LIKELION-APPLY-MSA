package com.likelionknu.applyserver.auth.data.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class AuthUserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val profileUrl: String?,
    val userRole: String,
    val disabled: Boolean,
    val disabledAt: LocalDateTime?,
    val lastAccessAt: LocalDateTime?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,

    val studentId: String?,
    val depart: String?,
    val grade: Int?,
    val phone: String?,
    val academicStatus: String?,

    val currentPart: String?,
    val currentGeneration: Int?
)