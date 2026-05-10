package com.likelionknu.applyserver.admin.data.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class AdminRecruitSummaryResponseDto(

    @field:JsonProperty("recruit_id")
    val recruitId: Long,

    val title: String,

    @field:JsonProperty("start_at")
    val startAt: LocalDateTime,

    @field:JsonProperty("end_at")
    val endAt: LocalDateTime,

    val submit: Long,

    val draft: Long
)