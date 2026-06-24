package com.likelionknu.applyserver.admin.data.dto.request

import jakarta.validation.constraints.NotBlank

data class AdminApplicationStatusRequest(
    @field:NotBlank
    val status: String
)