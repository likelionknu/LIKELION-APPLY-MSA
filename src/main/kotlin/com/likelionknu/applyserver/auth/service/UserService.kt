package com.likelionknu.applyserver.auth.service

import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.application.data.entity.MailHistory
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.application.data.repository.MailHistoryRepository
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository
import com.likelionknu.applyserver.auth.data.dto.request.ModifyProfileRequestDto
import com.likelionknu.applyserver.auth.data.dto.response.ProfileResponseDto
import com.likelionknu.applyserver.auth.data.entity.Profile
import com.likelionknu.applyserver.auth.data.entity.User
import com.likelionknu.applyserver.auth.exception.UserNotFoundException
import com.likelionknu.applyserver.auth.data.repository.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val mailHistoryRepository: MailHistoryRepository,
    private val applicationRepository: ApplicationRepository,
    private val recruitAnswerRepository: RecruitAnswerRepository
) {
    private val log = LoggerFactory.getLogger(UserService::class.java)

    @Transactional
    fun modifyUsersProfile(email: String, modifyProfileRequestDto: ModifyProfileRequestDto): ProfileResponseDto {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException()
        val profile = checkNotNull(user.profile)

        modifyProfileRequestDto.name?.let { user.name = it }
        modifyProfileRequestDto.depart?.let { profile.depart = it }
        modifyProfileRequestDto.studentId?.let { profile.studentId = it }
        modifyProfileRequestDto.grade?.let { profile.grade = it }
        modifyProfileRequestDto.phone?.let { profile.phone = it }
        modifyProfileRequestDto.status?.let { profile.status = it }

        log.info("[modifyUsersProfile] 사용자 상세 정보 수정: {}", email)

        return ProfileResponseDto(
            email = user.email,
            name = user.name,
            profileUrl = user.profileUrl,
            depart = profile.depart,
            studentId = profile.studentId,
            grade = profile.grade,
            phone = profile.phone,
            status = profile.status?.displayName
        )
    }

    fun getUsersProfile(email: String): ProfileResponseDto {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException()
        val profile = checkNotNull(user.profile)

        log.info("[getUsersProfile] 사용자 상세 정보 조회: {}", email)

        return ProfileResponseDto(
            email = user.email,
            name = user.name,
            profileUrl = user.profileUrl,
            depart = profile.depart,
            studentId = profile.studentId,
            grade = profile.grade,
            phone = profile.phone,
            status = profile.status?.displayName
        )
    }

    fun deleteUsersProfile(email: String) {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException()

        val mailHistoryList: List<MailHistory> = mailHistoryRepository.findAllByUser(user)
        for (mailHistory in mailHistoryList) {
            mailHistory.user = null
            mailHistoryRepository.save(mailHistory)
        }

        val applicationList: List<Application> = applicationRepository.findAllByUser(user)
        for (application in applicationList) {
            val recruitAnswerList: List<RecruitAnswer> = recruitAnswerRepository.findAllByApplication(application)
            recruitAnswerRepository.deleteAll(recruitAnswerList)
        }

        log.info("[deleteUsersProfile] 사용자 회원 탈퇴: {}", email)

        applicationRepository.deleteAll(applicationList)
        userRepository.delete(user)
    }
}