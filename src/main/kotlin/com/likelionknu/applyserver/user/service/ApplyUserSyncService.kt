package com.likelionknu.applyserver.user.service

import com.likelionknu.applyserver.auth.client.AuthUserClient
import com.likelionknu.applyserver.auth.data.dto.response.AuthUserResponse
import com.likelionknu.applyserver.auth.data.enums.StudentStatus
import com.likelionknu.applyserver.user.data.entity.ApplyUser
import com.likelionknu.applyserver.user.data.repository.ApplyUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplyUserSyncService(
    private val applyUserRepository: ApplyUserRepository,
    private val authUserClient: AuthUserClient
) {

    @Transactional
    fun getOrSync(email: String): ApplyUser {
        val authUser = getAuthUser(email)

        val applyUser = applyUserRepository.findByEmail(authUser.email)
            ?: ApplyUser(
                authEmail = authUser.email,
                email = authUser.email
            )

        applyUser.authEmail = authUser.email
        applyUser.email = authUser.email
        applyUser.name = authUser.name
        applyUser.profileUrl = authUser.profileUrl
        applyUser.phone = authUser.phone
        applyUser.depart = authUser.depart
        applyUser.grade = authUser.grade
        applyUser.studentId = authUser.studentId
        applyUser.status = toStudentStatus(authUser.academicStatus)

        return applyUserRepository.save(applyUser)
    }

    private fun getAuthUser(email: String): AuthUserResponse {
        val response = authUserClient.getUser(email)

        return response.data
            ?: throw IllegalStateException("AuthService 사용자 정보 응답이 비어 있습니다.")
    }

    private fun toStudentStatus(value: String?): StudentStatus? {
        return value?.let {
            runCatching { StudentStatus.valueOf(it) }.getOrNull()
        }
    }
}