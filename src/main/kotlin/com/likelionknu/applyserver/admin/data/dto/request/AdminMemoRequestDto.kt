package com.likelionknu.applyserver.admin.data.dto.request

import jakarta.validation.constraints.Size

data class AdminMemoRequestDto(

    @field:Size(max = 100)
    val memo: String
)