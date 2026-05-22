package com.likelionknu.applyserver.auth.service

import com.likelionknu.applyserver.auth.data.dto.request.ModifyProfileRequestDto
import com.likelionknu.applyserver.auth.data.dto.response.ProfileResponseDto
import com.likelionknu.applyserver.auth.exception.UserNotFoundException
import com.likelionknu.applyserver.user.data.repository.ApplyUserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserService(
    private val applyUserRepository: ApplyUserRepository
) {
    private val log = LoggerFactory.getLogger(UserService::class.java)

    @Transactional
    fun modifyUsersProfile(
        email: String,
        modifyProfileRequestDto: ModifyProfileRequestDto
    ): ProfileResponseDto {
        val applyUser = applyUserRepository.findByEmail(email)
            ?: throw UserNotFoundException()

        modifyProfileRequestDto.name?.let { applyUser.name = it }
        modifyProfileRequestDto.depart?.let { applyUser.depart = it }
        modifyProfileRequestDto.studentId?.let { applyUser.studentId = it }
        modifyProfileRequestDto.grade?.let { applyUser.grade = it }
        modifyProfileRequestDto.phone?.let { applyUser.phone = it }
        modifyProfileRequestDto.status?.let { applyUser.status = it }

        log.info("[modifyUsersProfile] 사용자 상세 정보 수정: {}", email)

        return ProfileResponseDto(
            email = applyUser.email,
            name = applyUser.name,
            profileUrl = applyUser.profileUrl,
            depart = applyUser.depart,
            studentId = applyUser.studentId,
            grade = applyUser.grade,
            phone = applyUser.phone,
            status = applyUser.status?.displayName
        )
    }

    fun getUsersProfile(email: String): ProfileResponseDto {
        val applyUser = applyUserRepository.findByEmail(email)
            ?: throw UserNotFoundException()

        log.info("[getUsersProfile] 사용자 상세 정보 조회: {}", email)

        return ProfileResponseDto(
            email = applyUser.email,
            name = applyUser.name,
            profileUrl = applyUser.profileUrl,
            depart = applyUser.depart,
            studentId = applyUser.studentId,
            grade = applyUser.grade,
            phone = applyUser.phone,
            status = applyUser.status?.displayName
        )
    }
}