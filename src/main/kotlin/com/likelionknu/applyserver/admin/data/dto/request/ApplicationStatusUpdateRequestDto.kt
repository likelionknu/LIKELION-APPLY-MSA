package com.likelionknu.applyserver.admin.data.dto.request

import com.likelionknu.applyserver.auth.data.enums.ApplicationEvaluation
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus

data class ApplicationStatusUpdateRequestDto(
    val status: ApplicationStatus?,
    val evaluation: ApplicationEvaluation?
)