package com.likelionknu.applyserver.admin.service

import com.likelionknu.applyserver.admin.data.dto.request.AdminMemoRequestDto
import com.likelionknu.applyserver.admin.data.dto.request.ApplicationStatusUpdateRequestDto
import com.likelionknu.applyserver.admin.data.exception.InvalidRequestException
import com.likelionknu.applyserver.application.data.exception.ApplicationNotFoundException
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.auth.data.enums.ApplicationEvaluation
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminApplicationService(
    private val applicationRepository: ApplicationRepository
) {

    /**
     * 운영진 메모 등록
     *
     * 해당 지원서에 운영진이 메모를 저장
     *
     * @param applicationId: 지원서 고유 ID
     * @param request: 관리자 메모 요청 DTO
     *
     * @throws ApplicationNotFoundException
     */
    @Transactional
    fun saveAdminMemo(applicationId: Long, request: AdminMemoRequestDto) {
        val application = applicationRepository.findById(applicationId)
            .orElseThrow(::ApplicationNotFoundException)

        application.updateNote(request.memo)
    }

    /**
     * 지원서 상태 변경
     *
     * 관리자가 지원서의 상태를 변경함
     * 상태(status) 변경 시 기존 평가(evaluation)는 null로 초기화됨
     *
     * @param applicationId: 지원서 고유 ID
     * @param status: 변경할 지원서 상태
     *
     * @throws ApplicationNotFoundException
     */
    @Transactional
    fun updateStatus(applicationId: Long, status: ApplicationStatus) {
        val application = applicationRepository.findById(applicationId)
            .orElseThrow(::ApplicationNotFoundException)

        application.changeStatus(status)
    }

    /**
     * 지원서 평가 변경
     *
     * 관리자가 지원서의 평가 값을 변경함
     *
     * @param applicationId: 지원서 고유 ID
     * @param evaluation: 변경할 평가 값
     *
     * @throws ApplicationNotFoundException
     */
    @Transactional
    fun updateEvaluation(applicationId: Long, evaluation: ApplicationEvaluation) {
        val application = applicationRepository.findById(applicationId)
            .orElseThrow(::ApplicationNotFoundException)

        application.updateEvaluation(evaluation)
    }

    /**
     * 지원서 상태 또는 평가 수정 (PATCH)
     *
     * Request Body는 아래 중 하나의 필드만 포함해야 함
     * - status: 지원서 상태 변경
     * - evaluation: 운영진 평가 변경
     *
     * 상태(status) 변경 시 평가(evaluation)는 null로 초기화됨
     *
     * 둘 다 없거나 둘 다 존재할 경우 예외 발생
     *
     * @param applicationId: 지원서 고유 ID
     * @param request: 상태/평가 수정 요청 DTO
     *
     * @throws ApplicationNotFoundException
     * @throws InvalidRequestException
     */
    @Transactional
    fun patch(applicationId: Long, request: ApplicationStatusUpdateRequestDto) {
        val status = request.status
        val evaluation = request.evaluation

        if ((status == null && evaluation == null) || (status != null && evaluation != null)) {
            throw InvalidRequestException()
        }

        if (status != null) {
            updateStatus(applicationId, status)
        } else {
            updateEvaluation(applicationId, evaluation!!)
        }
    }

    /**
     * 지원서 삭제
     *
     * 관리자가 특정 지원서를 강제 삭제
     *
     * @param applicationId: 지원서 고유 ID
     *
     * @throws ApplicationNotFoundException 지원서를 찾을 수 없을 경우
     */
    @Transactional
    fun deleteAdminApplication(applicationId: Long) {
        val application = applicationRepository.findById(applicationId)
            .orElseThrow(::ApplicationNotFoundException)

        applicationRepository.delete(application)
    }
}