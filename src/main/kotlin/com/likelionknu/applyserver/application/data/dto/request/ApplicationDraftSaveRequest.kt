package com.likelionknu.applyserver.application.data.dto.request

import com.likelionknu.applyserver.application.data.enums.ApplicantPart
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

data class ApplicationDraftSaveRequest(
    @field:NotNull
    val preferredPart: ApplicantPart?,

    @field:Valid
    val items: List<Item> = emptyList()
) {
    data class Item(
        val questionId: Long? = null,
        val answer: String? = null
    )
}