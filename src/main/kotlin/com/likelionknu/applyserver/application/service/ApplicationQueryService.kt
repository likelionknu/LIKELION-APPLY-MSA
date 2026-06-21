package com.likelionknu.applyserver.application.service

import com.likelionknu.applyserver.application.data.dto.response.ApplicationDetailResponse
import com.likelionknu.applyserver.application.data.dto.response.ApplicationSummaryResponse
import com.likelionknu.applyserver.application.data.exception.ApplicationNotFoundException
import com.likelionknu.applyserver.application.data.exception.InvalidApplicationAccessException
import com.likelionknu.applyserver.application.data.exception.UserNotFoundException
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository
import com.likelionknu.applyserver.user.service.ApplyUserSyncService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplicationQueryService(
    private val applicationRepository: ApplicationRepository,
    private val recruitAnswerRepository: RecruitAnswerRepository,
    private val applyUserSyncService: ApplyUserSyncService
) {

    @Transactional(readOnly = true)
    fun getMyApplications(email: String): List<ApplicationSummaryResponse> {
        val applyUser = applyUserSyncService.getOrSync(email)

        val applyUserId = applyUser.id
            ?: throw UserNotFoundException()

        val applications = applicationRepository.findAllWithRecruitByUserId(applyUserId)

        return applications.map { application ->
            ApplicationSummaryResponse(
                applicationId = application.id!!,
                recruitTitle = application.recruit.title,
                status = application.status.name,
                startAt = application.recruit.startAt,
                endAt = application.recruit.endAt
            )
        }
    }

    @Transactional(readOnly = true)
    fun getApplicationDetail(
        email: String,
        applicationId: Long
    ): ApplicationDetailResponse {
        val applyUser = applyUserSyncService.getOrSync(email)

        val application = applicationRepository.findById(applicationId)
            .orElseThrow { ApplicationNotFoundException() }

        if (application.user.id != applyUser.id) {
            throw InvalidApplicationAccessException()
        }

        val answers = recruitAnswerRepository.findByApplicationId(applicationId)

        return ApplicationDetailResponse(
            applicationId = application.id!!,
            recruitId = application.recruit.id!!,
            recruitTitle = application.recruit.title,
            status = application.status.name,
            startAt = application.recruit.startAt,
            endAt = application.recruit.endAt,
            submittedAt = application.submittedAt,
            answers = answers.map { answer ->
                ApplicationDetailResponse.ApplicationAnswerResponse(
                    questionId = answer.content.id!!,
                    question = answer.content.question,
                    answer = answer.answer
                )
            }
        )
    }
}