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
    val course: Int?,

    @JsonProperty("joined_at")
    val joinedAt: LocalDateTime?
) {
    companion object {
        fun from(authUserResponse: AuthUserResponse): ApplyUserProfileResponse {
            return ApplyUserProfileResponse(
                email = authUserResponse.email,
                name = authUserResponse.name,
                profileUrl = authUserResponse.profileUrl,
                role = authUserResponse.role,
                depart = authUserResponse.depart,
                grade = authUserResponse.grade,
                phone = authUserResponse.phone,
                studentId = authUserResponse.studentId,
                status = authUserResponse.academicStatus,
                part = authUserResponse.part,
                course = authUserResponse.course,
                joinedAt = authUserResponse.joinedAt
            )
        }
    }
}