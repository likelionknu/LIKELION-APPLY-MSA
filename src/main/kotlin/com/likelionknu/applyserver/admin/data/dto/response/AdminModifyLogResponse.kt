package com.likelionknu.applyserver.admin.data.dto.response

import com.likelionknu.applyserver.application.data.entity.ApplyModifyLog
import java.time.LocalDateTime

data class AdminModifyLogResponse(
    val id: String,
    val target: Target,
    val field: String,
    val fieldLabel: String,
    val before: String?,
    val after: String?,
    val actor: String,
    val at: LocalDateTime?
) {
    data class Target(
        val kind: String,
        val id: String
    )

    companion object {
        fun from(log: ApplyModifyLog): AdminModifyLogResponse {
            val applicationId = log.application?.id

            val target = if (applicationId != null) {
                Target(
                    kind = "application",
                    id = applicationId.toString()
                )
            } else {
                Target(
                    kind = "recruit",
                    id = log.recruit.id?.toString()
                        ?: throw IllegalStateException("공고 ID가 없습니다.")
                )
            }

            return AdminModifyLogResponse(
                id = log.id?.toString()
                    ?: throw IllegalStateException("변경 이력 ID가 없습니다."),
                target = target,
                field = log.field,
                fieldLabel = log.fieldLabel,
                before = log.beforeValue,
                after = log.afterValue,
                actor = log.user.name,
                at = log.createdAt
            )
        }
    }
}