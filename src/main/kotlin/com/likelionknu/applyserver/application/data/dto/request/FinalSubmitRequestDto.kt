package com.likelionknu.applyserver.application.data.dto.request

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class FinalSubmitRequestDto(
    @field:NotNull
    var recruitId: Long,

    @field:NotEmpty
    @field:Valid
    val items: List<Item>
) {
    data class Item(
        @field:NotNull
        var questionId: Long,

        @field:NotBlank
        val answer: String
    )
}