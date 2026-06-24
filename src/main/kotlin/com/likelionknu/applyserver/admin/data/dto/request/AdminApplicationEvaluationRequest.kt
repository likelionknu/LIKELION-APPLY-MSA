package com.likelionknu.applyserver.admin.data.dto.request

data class AdminApplicationEvaluationRequest(
    val documentEvaluation: String? = null,
    val interviewEvaluation: String? = null,
    val evaluation: String? = null
)