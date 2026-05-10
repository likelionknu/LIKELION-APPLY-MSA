package com.likelionknu.applyserver.admin.service

import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitApplicationResponseDto
import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.collections.map

@Service
@Transactional(readOnly = true)
class AdminRecruitApplicationService(
    private val applicationRepository: ApplicationRepository
) {

    /**
     * 모집 공고에 등록된 전체 지원서 조회
     *
     * 특정 모집 공고에 지원한 지원서 목록을 조회함
     * 지원서 정보와 함께 지원자 이름, 관리자 메모, 운영진 평가, 지원 상태,
     * 학과, 제출 일시 반환
     *
     * @param recruitId: 모집 공고 고유 ID
     * @return 해당 모집 공고의 지원서 목록
     */
    fun getApplicationsByRecruit(recruitId: Long): List<AdminRecruitApplicationResponseDto> {
        val applications = applicationRepository.findAllByRecruitIdWithUserProfile(recruitId)

        return applications.map { application ->
            AdminRecruitApplicationResponseDto(
                application.id!!,
                application.user.name,
                application.note,
                application.evaluation?.name,
                application.status?.name,
                application.user.profile?.depart,
                application.submittedAt
            )
        }
    }
}