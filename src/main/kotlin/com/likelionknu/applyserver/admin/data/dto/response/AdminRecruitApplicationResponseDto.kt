package com.likelionknu.applyserver.admin.data.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class AdminRecruitApplicationResponseDto(

    @get:JsonProperty("application_id")
    val applicationId: Long,

    val name: String,

    val memo: String?,

    val evaluation: String?,

    val status: String?,

    val depart: String?,

    @get:JsonProperty("submitted_at")
    val submittedAt: LocalDateTime?

)