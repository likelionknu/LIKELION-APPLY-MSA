package com.likelionknu.applyserver.recruit.controller

import com.likelionknu.applyserver.common.response.GlobalResponse
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitAvailabilityResponse
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitDetailResponse
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitListResponse
import com.likelionknu.applyserver.recruit.service.RecruitService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/recruits")
class RecruitController(
    private val recruitService: RecruitService
) {

    @GetMapping
    @Operation(summary = "모든 모집 공고 조회")
    fun getRecruits(): GlobalResponse<List<RecruitListResponse>> {
        return GlobalResponse.ok(recruitService.getRecruits())
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "공고 지원 가능 여부 조회")
    fun checkAvailability(@PathVariable id: Long): GlobalResponse<RecruitAvailabilityResponse> {
        return GlobalResponse.ok(recruitService.checkAvailability(id))
    }

    @GetMapping("/{id}/questions")
    @Operation(summary = "공고 질문 목록 조회")
    fun getRecruitQuestions(@PathVariable id: Long): GlobalResponse<RecruitDetailResponse> {
        return GlobalResponse.ok(recruitService.getRecruitQuestions(id))
    }
}