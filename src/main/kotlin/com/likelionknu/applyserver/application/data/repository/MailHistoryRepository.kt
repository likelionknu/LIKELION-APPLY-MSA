package com.likelionknu.applyserver.application.data.repository

import com.likelionknu.applyserver.application.data.entity.MailHistory
import com.likelionknu.applyserver.auth.data.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface MailHistoryRepository : JpaRepository<MailHistory, Long> {
    fun findAllByUser(user: User): List<MailHistory>
}