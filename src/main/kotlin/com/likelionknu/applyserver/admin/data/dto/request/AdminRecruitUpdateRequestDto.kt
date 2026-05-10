package com.likelionknu.applyserver.admin.data.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class AdminRecruitUpdateRequestDto(
    val title: String,

    @field:JsonProperty("start_at")
    val startAt: LocalDateTime,

    @field:JsonProperty("end_at")
    val endAt: LocalDateTime,

    val questions: List<Item>
) {
    data class Item(
        val question: String,
        val priority: Int
    )
}