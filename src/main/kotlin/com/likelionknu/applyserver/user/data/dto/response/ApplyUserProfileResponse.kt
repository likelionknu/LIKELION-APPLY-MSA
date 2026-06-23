package com.likelionknu.applyserver.user.data.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.likelionknu.applyserver.auth.data.dto.response.AuthUserResponse
import java.time.LocalDateTime

data class ApplyUserProfileResponse(
    val email: String,
    val name: String,

    @JsonProperty("profile_url")
    val profileUrl: String?,

    val role: String,

    val depart: String?,
    val grade: Int?,
    val phone: String?,

    @JsonProperty("student_id")
    val studentId: String?,

    val status: String?,

    val part: String?,
    val generation: Int?,

    @JsonProperty("last_access_at")
    val lastAccessAt: LocalDateTime?
) {
    companion object {
        fun from(authUserResponse: AuthUserResponse): ApplyUserProfileResponse {
            return ApplyUserProfileResponse(
                email = authUserResponse.email,
                name = authUserResponse.name,
                profileUrl = authUserResponse.profileUrl,
                role = authUserResponse.userRole,
                depart = authUserResponse.depart,
                grade = authUserResponse.grade,
                phone = authUserResponse.phone,
                studentId = authUserResponse.studentId,
                status = authUserResponse.academicStatus,
                part = authUserResponse.currentPart,
                generation = authUserResponse.currentGeneration,
                lastAccessAt = authUserResponse.lastAccessAt
            )
        }
    }
}