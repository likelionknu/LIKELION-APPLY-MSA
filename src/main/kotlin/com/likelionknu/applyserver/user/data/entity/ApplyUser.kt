package com.likelionknu.applyserver.user.data.entity

import com.likelionknu.applyserver.auth.data.enums.StudentStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "apply_user")
class ApplyUser(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "auth_email", unique = true)
    var authEmail: String? = null,

    @Column(nullable = false, unique = true)
    var email: String = "",

    @Column(nullable = false)
    var name: String = "",

    @Column(name = "profile_url")
    var profileUrl: String? = null,

    @Column
    var phone: String? = null,

    @Column
    var depart: String? = null,

    @Column
    var grade: Int? = null,

    @Column(name = "student_id")
    var studentId: String? = null,

    @Enumerated(EnumType.STRING)
    @Column
    var status: StudentStatus? = null,

    @Column(name = "last_access_at", nullable = false)
    var lastAccessAt: LocalDateTime = LocalDateTime.now(),

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null
)