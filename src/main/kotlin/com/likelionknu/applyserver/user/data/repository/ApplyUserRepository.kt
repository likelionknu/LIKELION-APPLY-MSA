package com.likelionknu.applyserver.user.data.repository

import com.likelionknu.applyserver.user.data.entity.ApplyUser
import org.springframework.data.jpa.repository.JpaRepository

interface ApplyUserRepository : JpaRepository<ApplyUser, Long> {
    fun findByEmail(email: String): ApplyUser?
    fun findByAuthEmail(authEmail: String): ApplyUser?
}