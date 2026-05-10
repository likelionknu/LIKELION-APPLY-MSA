package com.likelionknu.applyserver.application.service

import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ApplicationCancelService(
    private val applicationRepository: ApplicationRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 지원서 회수 기능
     *
     * 제출한 지원서를 회수합니다.
     *
     * @param userId 사용자 고유 ID
     * @param recruitId 공고 고유 ID
     */
    @Transactional
    fun cancel(userId: Long, recruitId: Long) {
        val application: Application = applicationRepository.findByUserIdAndRecruitId(userId, recruitId)
            ?: throw IllegalStateException("회수할 지원서가 없습니다.")

        if (application.status == ApplicationStatus.CANCELED) return

        application.changeStatus(ApplicationStatus.CANCELED)
        log.info("[cancel] 지원서 회수 요청: {} {}", application.recruit.title, application.user.email)
    }

    /**
     * 지원서 회수 복구 기능
     *
     * 회수한 지원서를 철회합니다.
     *
     * @param userId 사용자 고유 ID
     * @param recruitId 공고 고유 ID
     */
    @Transactional
    fun restore(userId: Long, recruitId: Long) {
        val application: Application = applicationRepository.findByUserIdAndRecruitIdAndStatus(userId, recruitId, ApplicationStatus.CANCELED)
            ?: throw IllegalStateException("복구 가능한 지원서가 없습니다.")

        val now = LocalDateTime.now()
        val isOpen = !now.isBefore(application.recruit.startAt) && !now.isAfter(application.recruit.endAt)

        if (!isOpen) throw GlobalException(ErrorCode.FORBIDDEN)

        application.restoreFromCanceled()
    }
}