package com.likelionknu.applyserver.admin.data.dto.response

import com.likelionknu.applyserver.application.data.entity.Application

data class AdminFinalPassedResponse(
    val applicationId: Long,
    val userId: Long,
    val email: String,
    val name: String,
    val studentId: String?,
    val preferredPart: String?,
    val generation: Int
) {
    companion object {
        fun from(
            application: Application
        ): AdminFinalPassedResponse {
            return AdminFinalPassedResponse(
                applicationId = application.id
                    ?: throw IllegalStateException("지원서 ID가 없습니다."),
                userId = application.user.id
                    ?: throw IllegalStateException("사용자 ID가 없습니다."),
                email = application.user.authEmail
                    ?: application.user.email,
                name = application.user.name,
                studentId = application.user.studentId,
                preferredPart = application.preferredPart?.value,
                generation = application.recruit.generation
            )
        }
    }
}