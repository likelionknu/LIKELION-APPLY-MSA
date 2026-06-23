package com.likelionknu.applyserver.admin.service

import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitListResponse
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.auth.data.enums.ApplicationEvaluation
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminRecruitService(
    private val recruitRepository: RecruitRepository,
    private val recruitContentRepository: RecruitContentRepository,
    private val applicationRepository: ApplicationRepository
) {

    @Transactional(readOnly = true)
    fun getRecruits(): List<AdminRecruitListResponse> {
        return recruitRepository.findAllByDeletedAtIsNull()
            .map { recruit ->
                val recruitId = recruit.id
                    ?: throw IllegalStateException("모집 공고 ID가 없습니다.")

                AdminRecruitListResponse.from(
                    recruit = recruit,
                    questionCount = recruitContentRepository.countByRecruitId(recruitId),
                    submit = applicationRepository.countByRecruitIdAndStatus(
                        recruitId,
                        ApplicationStatus.SUBMITTED
                    ),
                    draft = applicationRepository.countByRecruitIdAndStatus(
                        recruitId,
                        ApplicationStatus.DRAFT
                    ),
                    cancel = applicationRepository.countByRecruitIdAndStatus(
                        recruitId,
                        ApplicationStatus.CANCELED
                    ),
                    pass = applicationRepository.countByRecruitIdAndStatusAndEvaluation(
                        recruitId,
                        ApplicationStatus.SUBMITTED,
                        ApplicationEvaluation.PASS
                    ),
                    fail = applicationRepository.countByRecruitIdAndStatusAndEvaluation(
                        recruitId,
                        ApplicationStatus.SUBMITTED,
                        ApplicationEvaluation.FAIL
                    ),
                    hold = applicationRepository.countByRecruitIdAndStatusAndEvaluation(
                        recruitId,
                        ApplicationStatus.SUBMITTED,
                        ApplicationEvaluation.HOLD
                    ),
                    notReviewed = applicationRepository.countByRecruitIdAndStatusAndEvaluationIsNull(
                        recruitId,
                        ApplicationStatus.SUBMITTED
                    )
                )
            }
    }
}