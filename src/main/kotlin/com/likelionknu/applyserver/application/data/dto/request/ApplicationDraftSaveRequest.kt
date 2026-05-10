package com.likelionknu.applyserver.application.data.dto.request

data class ApplicationDraftSaveRequest(
    val questionId: Long? = null,
    val answer: String? = null
)