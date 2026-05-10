package com.likelionknu.applyserver.application.data.dto.response

import java.time.LocalDateTime

data class ApplicationSummaryResponse(
    val applicationId: Long,
    val recruitTitle: String,
    val status: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
)