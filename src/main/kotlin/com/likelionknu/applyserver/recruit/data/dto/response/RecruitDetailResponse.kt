package com.likelionknu.applyserver.recruit.data.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

class RecruitDetailResponse (
        val title: String,

        @JsonProperty("start_at")
        val startAt: String,

        @JsonProperty("end_at")
        val endAt: String,

        val questions: List<RecruitQuestionResponse>

)