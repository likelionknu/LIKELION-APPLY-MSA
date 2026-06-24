package com.likelionknu.applyserver.admin.data.dto.response

import com.likelionknu.applyserver.recruit.data.entity.Recruit
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent
import java.time.LocalDateTime

data class AdminRecruitDetailResponse(
    val id: Long?,
    val title: String,
    val generation: Int,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val documentResultAt: LocalDateTime,
    val finalResultAt: LocalDateTime,
    val questions: List<AdminRecruitQuestionResponse>,
    val createdAt: LocalDateTime?
) {
    companion object {
        fun from(
            recruit: Recruit,
            contents: List<RecruitContent>
        ): AdminRecruitDetailResponse {
            return AdminRecruitDetailResponse(
                id = recruit.id,
                title = recruit.title,
                generation = recruit.generation,
                startAt = recruit.startAt,
                endAt = recruit.endAt,
                documentResultAt = recruit.documentResultAt,
                finalResultAt = recruit.finalResultAt,
                questions = contents.map(AdminRecruitQuestionResponse::from),
                createdAt = recruit.createdAt
            )
        }
    }
}

data class AdminRecruitQuestionResponse(
    val question: String,
    val priority: Int,
    val required: Boolean,
    val minLength: Int?,
    val maxLength: Int?
) {
    companion object {
        fun from(content: RecruitContent): AdminRecruitQuestionResponse {
            return AdminRecruitQuestionResponse(
                question = content.question,
                priority = content.priority,
                required = content.required,
                minLength = content.minLength,
                maxLength = content.maxLength
            )
        }
    }
}