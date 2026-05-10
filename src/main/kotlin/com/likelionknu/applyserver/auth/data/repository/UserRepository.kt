package com.likelionknu.applyserver.auth.data.repository


import com.likelionknu.applyserver.auth.data.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User
    fun findOptionalByEmail(email: String): Optional<User>
}