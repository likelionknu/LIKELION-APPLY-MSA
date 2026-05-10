package com.likelionknu.applyserver.application.service

import com.likelionknu.applyserver.application.data.dto.response.ApplicationDetailResponse
import com.likelionknu.applyserver.application.data.dto.response.ApplicationSummaryResponse
import com.likelionknu.applyserver.application.data.exception.InvalidApplicationAccessException
import com.likelionknu.applyserver.application.data.exception.UserNotFoundException
import com.likelionknu.applyserver.application.data.exception.ApplicationNotFoundException
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository
import com.likelionknu.applyserver.auth.data.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplicationQueryService(
    private val applicationRepository: ApplicationRepository,
    private val userRepository: UserRepository,
    private val recruitAnswerRepository: RecruitAnswerRepository
) {

    @Transactional(readOnly = true)
    fun getMyApplications(email: String): List<ApplicationSummaryResponse> {
        val user = userRepository.findOptionalByEmail(email)
            .orElseThrow { UserNotFoundException() }

        val applications = applicationRepository.findAllWithRecruitByUserId(user.id!!)

        return applications.map { a ->
            ApplicationSummaryResponse(
                applicationId = a.id!!,
                recruitTitle = a.recruit.title,
                status = a.status!!.name,
                startAt = a.recruit.startAt,
                endAt = a.recruit.endAt
            )
        }
    }

    @Transactional(readOnly = true)
    fun getApplicationDetail(email: String, applicationId: Long): ApplicationDetailResponse {
        val user = userRepository.findOptionalByEmail(email)
            .orElseThrow { UserNotFoundException() }

        val application = applicationRepository.findById(applicationId)
            .orElseThrow { ApplicationNotFoundException() }

        if (application.user.id != user.id) {
            throw InvalidApplicationAccessException()
        }

        val answers = recruitAnswerRepository.findByApplicationId(applicationId)

        return ApplicationDetailResponse(
            applicationId = application.id!!,
            recruitId = application.recruit.id!!,
            recruitTitle = application.recruit.title,
            status = application.status!!.name,
            startAt = application.recruit.startAt,
            endAt = application.recruit.endAt,
            submittedAt = application.submittedAt,
            answers = answers.map { a ->
                ApplicationDetailResponse.ApplicationAnswerResponse(
                    questionId = a.content.id!!,
                    question = a.content.question,
                    answer = a.answer
                )
            }
        )
    }
}