package com.likelionknu.applyserver.application.data.entity

import com.likelionknu.applyserver.recruit.data.entity.Recruit
import com.likelionknu.applyserver.user.data.entity.ApplyUser
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "apply_modify_log",
    indexes = [
        Index(
            name = "idx_modify_log_recruit",
            columnList = "recruit_id, created_at"
        ),
        Index(
            name = "idx_modify_log_application",
            columnList = "application_id"
        )
    ]
)
class ApplyModifyLog(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id", nullable = false)
    val recruit: Recruit,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    val application: Application? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: ApplyUser,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null
)