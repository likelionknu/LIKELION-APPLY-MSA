package com.likelionknu.applyserver.application.data.dto.request

import com.likelionknu.applyserver.application.data.enums.ApplicantPart
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class FinalSubmitRequestDto(
    @field:NotNull
    val recruitId: Long,

    @field:NotNull
    val preferredPart: ApplicantPart?,

    @field:NotEmpty
    @field:Valid
    val items: List<Item>
) {
    data class Item(
        @field:NotNull
        val questionId: Long,

        @field:NotBlank
        val answer: String
    )
}