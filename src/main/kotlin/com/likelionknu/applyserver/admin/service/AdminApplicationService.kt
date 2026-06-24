package com.likelionknu.applyserver.admin.service

import com.likelionknu.applyserver.admin.data.dto.response.AdminApplicationResponse
import com.likelionknu.applyserver.application.data.exception.RecruitNotFoundException
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminApplicationService(
    private val applicationRepository: ApplicationRepository,
    private val recruitRepository: RecruitRepository
) {

    @Transactional(readOnly = true)
    fun getApplications(
        recruitId: Long
    ): List<AdminApplicationResponse> {
        val recruit = recruitRepository.findById(recruitId)
            .orElseThrow { RecruitNotFoundException() }

        if (recruit.deletedAt != null) {
            throw RecruitNotFoundException()
        }

        return applicationRepository
            .findAllWithUserAndAnswersByRecruitId(recruitId)
            .map(AdminApplicationResponse::from)
    }
}