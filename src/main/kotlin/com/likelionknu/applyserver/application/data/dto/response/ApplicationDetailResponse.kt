package com.likelionknu.applyserver.application.data.dto.response

import java.time.LocalDateTime

data class ApplicationDetailResponse(
    val applicationId: Long,
    val recruitId: Long,
    val recruitTitle: String,
    val status: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val submittedAt: LocalDateTime?,
    val answers: List<ApplicationAnswerResponse>
) {
    data class ApplicationAnswerResponse(
        val questionId: Long,
        val question: String,
        val answer: String
    )
}