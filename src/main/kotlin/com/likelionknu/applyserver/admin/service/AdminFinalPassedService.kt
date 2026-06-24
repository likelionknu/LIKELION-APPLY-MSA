package com.likelionknu.applyserver.admin.service

import com.likelionknu.applyserver.admin.data.dto.response.AdminFinalPassedResponse
import com.likelionknu.applyserver.application.data.exception.RecruitNotFoundException
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.auth.data.enums.ApplicationEvaluation
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminFinalPassedService(
    private val recruitRepository: RecruitRepository,
    private val applicationRepository: ApplicationRepository
) {

    @Transactional(readOnly = true)
    fun getFinalPassedApplicants(
        recruitId: Long
    ): List<AdminFinalPassedResponse> {
        val recruit = recruitRepository.findById(recruitId)
            .orElseThrow { RecruitNotFoundException() }

        if (recruit.deletedAt != null) {
            throw RecruitNotFoundException()
        }

        return applicationRepository
            .findFinalPassedApplications(
                recruitId = recruitId,
                status = ApplicationStatus.SUBMITTED,
                evaluation = ApplicationEvaluation.PASS
            )
            .map(AdminFinalPassedResponse::from)
    }
}