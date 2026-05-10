package com.likelionknu.applyserver.application.service

import com.likelionknu.applyserver.application.data.dto.request.ApplicationDraftSaveRequest
import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplicationAnswerService(
    private val recruitAnswerRepository: RecruitAnswerRepository,
    private val recruitContentRepository: RecruitContentRepository
) {
    @Transactional
    fun replaceAnswers(application: Application, requests: List<ApplicationDraftSaveRequest>?) {
        recruitAnswerRepository.deleteByApplication_Id(application.id!!)

        requests ?: return

        for (req in requests) {
            val questionId = req.questionId ?: continue
            val answer = req.answer?.takeIf { it.isNotBlank() } ?: continue

            val content = recruitContentRepository.findById(questionId)
                .orElseThrow { IllegalArgumentException("질문을 찾을 수 없습니다. questionId=$questionId") }

            recruitAnswerRepository.save(
                RecruitAnswer(
                    application = application,
                    content = content,
                    answer = answer
                )
            )
        }
    }

    fun getAnswers(application: Application): List<RecruitAnswer> {
        return recruitAnswerRepository.findByApplication(application)
    }
}