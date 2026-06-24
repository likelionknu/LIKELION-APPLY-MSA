package com.likelionknu.applyserver.admin.data.dto.request

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class AdminRecruitCreateRequest(
    @field:NotBlank
    @field:Size(max = 40)
    val title: String,

    @field:Positive
    val generation: Int,

    @field:NotNull
    val startAt: LocalDateTime?,

    @field:NotNull
    val endAt: LocalDateTime?,

    @field:NotNull
    val documentResultAt: LocalDateTime?,

    @field:NotNull
    val finalResultAt: LocalDateTime?,

    @field:NotEmpty
    @field:Valid
    val questions: List<Question>
) {
    data class Question(
        @field:NotBlank
        val question: String,

        @field:Positive
        val priority: Int,

        val required: Boolean,

        @field:NotNull
        @field:Min(10)
        @field:Max(1000)
        val minLength: Int?,

        @field:NotNull
        @field:Min(10)
        @field:Max(1000)
        val maxLength: Int?
    )
}