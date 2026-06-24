package com.likelionknu.applyserver.application.service

import com.likelionknu.applyserver.application.data.dto.request.ApplicationDraftSaveRequest
import com.likelionknu.applyserver.application.data.dto.request.FinalSubmitRequestDto
import com.likelionknu.applyserver.application.data.exception.EmptyAnswerException
import com.likelionknu.applyserver.application.data.exception.InvalidApplicationQuestionException
import com.likelionknu.applyserver.application.data.exception.RecruitContentNotFoundException
import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository
import org.springframework.stereotype.Service

@Service
class ApplicationAnswerValidator(
    private val recruitContentRepository: RecruitContentRepository
) {
    companion object {
        private const val DEFAULT_MAX_LENGTH = 800
    }

    fun validateDraft(
        recruitId: Long,
        items: List<ApplicationDraftSaveRequest.Item>
    ) {
        items.forEach { item ->
            val questionId = item.questionId ?: return@forEach
            val answer = item.answer
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: return@forEach

            val content = getContent(questionId)

            validateQuestionBelongsToRecruit(content, recruitId)
            validateAnswerLength(content, answer)
        }
    }

    fun validateFinalSubmit(
        recruitId: Long,
        items: List<FinalSubmitRequestDto.Item>
    ) {
        if (items.isEmpty()) {
            throw EmptyAnswerException()
        }

        val contents = recruitContentRepository
            .findByRecruitIdOrderByPriorityAsc(recruitId)

        val contentMap = contents.associateBy { it.id }

        val answerMap = items.associate {
            it.questionId to it.answer.trim()
        }

        contents
            .filter { it.required }
            .forEach { content ->
                val contentId = content.id
                    ?: throw RecruitContentNotFoundException()

                val answer = answerMap[contentId]

                if (answer.isNullOrBlank()) {
                    throw EmptyAnswerException()
                }
            }

        items.forEach { item ->
            val answer = item.answer.trim()

            if (answer.isBlank()) {
                throw EmptyAnswerException()
            }

            val content = contentMap[item.questionId]
                ?: throw InvalidApplicationQuestionException()

            validateQuestionBelongsToRecruit(content, recruitId)
            validateAnswerLength(content, answer)
        }
    }

    private fun getContent(questionId: Long): RecruitContent {
        return recruitContentRepository.findById(questionId)
            .orElseThrow { RecruitContentNotFoundException() }
    }

    private fun validateQuestionBelongsToRecruit(
        content: RecruitContent,
        recruitId: Long
    ) {
        if (content.recruit?.id != recruitId) {
            throw InvalidApplicationQuestionException()
        }
    }

    private fun validateAnswerLength(
        content: RecruitContent,
        answer: String
    ) {
        val minLength = content.minLength
        val maxLength = content.maxLength ?: DEFAULT_MAX_LENGTH

        if (minLength != null && answer.length < minLength) {
            throw GlobalException(ErrorCode.INVALID_REQUEST)
        }

        if (answer.length > maxLength) {
            throw GlobalException(ErrorCode.INVALID_REQUEST)
        }
    }
}