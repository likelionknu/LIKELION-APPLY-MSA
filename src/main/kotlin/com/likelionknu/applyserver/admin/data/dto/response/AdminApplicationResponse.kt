package com.likelionknu.applyserver.admin.data.dto.response

import com.likelionknu.applyserver.application.data.entity.Application
import java.time.LocalDateTime

data class AdminApplicationResponse(
    val id: Long,
    val recruitId: Long,
    val applicantUserId: Long,
    val applicantName: String,
    val applicantEmail: String,
    val studentId: String?,
    val preferredPart: String?,
    val status: String,
    val documentEvaluation: String,
    val interviewEvaluation: String,
    val evaluation: String,
    val memo: String?,
    val answers: List<Answer>,
    val submittedAt: LocalDateTime
) {
    data class Answer(
        val questionId: Long,
        val question: String,
        val answer: String,
        val priority: Int
    )

    companion object {
        fun from(application: Application): AdminApplicationResponse {
            val answers = application.answers
                .filterNotNull()
                .sortedBy { it.content.priority }
                .map { answer ->
                    Answer(
                        questionId = answer.content.id
                            ?: throw IllegalStateException("질문 ID가 없습니다."),
                        question = answer.content.question,
                        answer = answer.answer,
                        priority = answer.content.priority
                    )
                }

            return AdminApplicationResponse(
                id = application.id
                    ?: throw IllegalStateException("지원서 ID가 없습니다."),
                recruitId = application.recruit.id
                    ?: throw IllegalStateException("공고 ID가 없습니다."),
                applicantUserId = application.user.id
                    ?: throw IllegalStateException("지원자 ID가 없습니다."),
                applicantName = application.user.name,
                applicantEmail = application.user.email,
                studentId = application.user.studentId,
                preferredPart = application.preferredPart?.value,
                status = application.status.name.lowercase(),
                documentEvaluation = application.documentEvaluation
                    ?.name
                    ?.lowercase()
                    ?: "not_reviewed",
                interviewEvaluation = application.interviewEvaluation
                    ?.name
                    ?.lowercase()
                    ?: "not_reviewed",
                evaluation = application.evaluation
                    ?.name
                    ?.lowercase()
                    ?: "not_reviewed",
                memo = application.note,
                answers = answers,
                submittedAt = application.submittedAt
            )
        }
    }
}