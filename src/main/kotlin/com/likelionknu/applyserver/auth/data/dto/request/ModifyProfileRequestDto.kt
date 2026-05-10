package com.likelionknu.applyserver.auth.data.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.likelionknu.applyserver.auth.data.enums.StudentStatus
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class ModifyProfileRequestDto(
    @field:Size(min = 2, max = 4)
    @field:Pattern(regexp = "^[ㄱ-ㅎ가-힣]*$")
    val name: String? = null,

    @field:Size(min = 4, max = 20)
    val depart: String? = null,

    @field:Size(min = 8, max = 9)
    @field:Pattern(regexp = "^[0-9]*$")
    @JsonProperty("student_id")
    val studentId: String? = null,

    @field:Max(4)
    @field:Positive
    val grade: Int? = null,

    @field:Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$")
    val phone: String? = null,

    val status: StudentStatus? = null
)