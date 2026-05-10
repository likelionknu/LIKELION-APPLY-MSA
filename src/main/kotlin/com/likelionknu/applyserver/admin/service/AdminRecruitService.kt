package com.likelionknu.applyserver.admin.service

import com.likelionknu.applyserver.admin.data.dto.request.AdminRecruitUpdateRequestDto
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitDetailResponseDto
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitSummaryResponseDto
import com.likelionknu.applyserver.admin.data.exception.InvalidRecruitUpdateRequestException
import com.likelionknu.applyserver.application.data.exception.RecruitNotFoundException
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.recruit.data.entity.Recruit
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent
import com.likelionknu.applyserver.recruit.data.exception.RecruitHasApplicationException
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminRecruitService (
    private val recruitRepository: RecruitRepository,
    private val recruitContentRepository: RecruitContentRepository,
    private val applicationRepository: ApplicationRepository
) {
    /**
     * 특정 모집 공고 상세 정보 조회
     *
     * @param recruitId: 모집 공고 ID
     * @return 모집 공고 상세 정보
     *
     * @throws RecruitNotFoundException
     */
    fun getRecruitDetail(recruitId: Long): AdminRecruitDetailResponseDto {
        val recruit = recruitRepository.findById(recruitId)
            .orElseThrow { RecruitNotFoundException() }

        val questions = recruitContentRepository
            .findByRecruitIdOrderByPriorityAsc(recruitId)
            .map { it.question }

        return AdminRecruitDetailResponseDto(
            title = recruit.title,
            startAt = recruit.startAt,
            endAt = recruit.endAt,
            questions = questions
        )
    }

    /**
     * 모집 공고 현황 조회
     *
     * 모집 공고별 지원 현황(제출, 임시저장 등) 정보를 조회함
     *
     * @return 모집 공고 요약 목록
     */
    fun getRecruitSummaries(): List<AdminRecruitSummaryResponseDto> {
        return applicationRepository.findRecruitSummary()
    }

    /**
     * 모집 공고 등록
     *
     * 모집 공고와 질문 목록 생성
     *
     * @param request: 모집 공고 생성 요청 DTO
     *
     * @throws InvalidRecruitUpdateRequestException
     */
    @Transactional
    fun createRecruit(request: AdminRecruitUpdateRequestDto) {
        validateUpdateRequest(request)

        val recruit = recruitRepository.save(
            Recruit(
                id = null,
                title = request.title,
                startAt = request.startAt,
                endAt = request.endAt
            )
        )

        val contents = request.questions.map {
            RecruitContent(
                id = null,
                recruit = recruit,
                question = it.question,
                priority = it.priority,
                required = true
            )
        }

        recruitContentRepository.saveAll(contents)
    }

    /**
     * 특정 모집 공고 수정
     *
     * 지원서 상태 여부에 관계없이 지원자가 한 명이라도 있다면 수정 거절
     *
     * @param recruitId: 모집 공고 ID
     * @param request: 수정 요청 DTO
     *
     * @throws RecruitNotFoundException
     * @throws RecruitHasApplicationException
     */
    @Transactional
    fun updateRecruit(recruitId: Long, request: AdminRecruitUpdateRequestDto) {
        validateUpdateRequest(request)

        if (applicationRepository.existsByRecruitIdAndStatusNot(recruitId, ApplicationStatus.CANCELED)) {
            throw RecruitHasApplicationException()
        }

        val recruit = recruitRepository.findById(recruitId)
            .orElseThrow { RecruitNotFoundException() }

        recruit.title = request.title
        recruit.startAt = request.startAt
        recruit.endAt = request.endAt

        recruitContentRepository.deleteAllByRecruitId(recruitId)

        val contents = request.questions.map {
            RecruitContent(
                id = null,
                recruit = recruit,
                question = it.question,
                priority = it.priority,
                required = true
            )
        }

        recruitContentRepository.saveAll(contents)
    }

    /**
     * 특정 모집 공고 삭제
     *
     * 지원서 상태 여부에 관계없이 지원자가 한 명이라도 있다면 삭제 거절
     *
     * @param recruitId: 모집 공고 ID
     *
     * @throws RecruitNotFoundException
     * @throws RecruitHasApplicationException
     */
    @Transactional
    fun deleteRecruit(recruitId: Long) {
        if (applicationRepository.existsByRecruitId(recruitId)) {
            throw RecruitHasApplicationException()
        }

        if (!recruitRepository.existsById(recruitId)) {
            throw RecruitNotFoundException()
        }

        recruitContentRepository.deleteAllByRecruitId(recruitId)
        recruitRepository.deleteById(recruitId)
    }


    private fun validateUpdateRequest(request: AdminRecruitUpdateRequestDto) {
        if (request.title.isBlank()) {
            throw InvalidRecruitUpdateRequestException("title은 필수입니다.")
        }
        if (request.startAt.isAfter(request.endAt)) {
            throw InvalidRecruitUpdateRequestException("start_at은 end_at보다 이후일 수 없습니다.")
        }
        if (request.questions.isEmpty()) {
            throw InvalidRecruitUpdateRequestException("questions는 최소 1개 이상이어야 합니다.")
        }

        val priorities = mutableSetOf<Int>()
        request.questions.forEach {
            if (it.question.isBlank()) {
                throw InvalidRecruitUpdateRequestException("question은 필수입니다.")
            }
            if (!priorities.add(it.priority)) {
                throw InvalidRecruitUpdateRequestException("priority가 중복되었습니다: ${it.priority}")
            }
        }
    }
}