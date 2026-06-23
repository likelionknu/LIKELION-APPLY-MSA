package com.likelionknu.applyserver.admin.data.dto.response

import com.likelionknu.applyserver.recruit.data.entity.Recruit
import java.time.LocalDateTime

data class AdminRecruitListResponse(
    val id: Long?,
    val title: String,
    val generation: Int,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val documentResultAt: LocalDateTime,
    val finalResultAt: LocalDateTime,
    val status: String,
    val questionCount: Long,
    val submit: Long,
    val draft: Long,
    val cancel: Long,
    val pass: Long,
    val fail: Long,
    val hold: Long,
    val notReviewed: Long
) {
    companion object {
        fun from(
            recruit: Recruit,
            questionCount: Long,
            submit: Long,
            draft: Long,
            cancel: Long,
            pass: Long,
            fail: Long,
            hold: Long,
            notReviewed: Long
        ): AdminRecruitListResponse {
            return AdminRecruitListResponse(
                id = recruit.id,
                title = recruit.title,
                generation = recruit.generation,
                startAt = recruit.startAt,
                endAt = recruit.endAt,
                documentResultAt = recruit.documentResultAt,
                finalResultAt = recruit.finalResultAt,
                status = getStatus(recruit),
                questionCount = questionCount,
                submit = submit,
                draft = draft,
                cancel = cancel,
                pass = pass,
                fail = fail,
                hold = hold,
                notReviewed = notReviewed
            )
        }

        private fun getStatus(recruit: Recruit): String {
            val now = LocalDateTime.now()

            return when {
                now.isBefore(recruit.startAt) -> "upcoming"
                now.isAfter(recruit.endAt) -> "closed"
                else -> "open"
            }
        }
    }
}