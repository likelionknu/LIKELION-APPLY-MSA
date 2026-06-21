package com.likelionknu.applyserver.recruit.data.dto.response

import com.likelionknu.applyserver.recruit.data.entity.Recruit
import com.likelionknu.applyserver.recruit.data.enums.RecruitStatus
import java.time.LocalDateTime

data class RecruitResponse(
    val id: Long?,
    val title: String,
    val generation: Int,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val documentResultAt: LocalDateTime,
    val finalResultAt: LocalDateTime,
    val status: RecruitStatus
) {
    companion object {
        fun from(recruit: Recruit): RecruitResponse {
            val now = LocalDateTime.now()

            val status = when {
                now.isBefore(recruit.startAt) -> RecruitStatus.UPCOMING
                now.isAfter(recruit.endAt) -> RecruitStatus.CLOSED
                else -> RecruitStatus.OPEN
            }

            return RecruitResponse(
                id = recruit.id,
                title = recruit.title,
                generation = recruit.generation,
                startAt = recruit.startAt,
                endAt = recruit.endAt,
                documentResultAt = recruit.documentResultAt,
                finalResultAt = recruit.finalResultAt,
                status = status
            )
        }
    }
}