package com.likelionknu.applyserver.application.data.entity

import com.likelionknu.applyserver.user.data.entity.ApplyUser
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "apply_mail_history")
class MailHistory(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    val subject: String,

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    val body: String,

    @Column(nullable = false)
    val recipient: String,

    @Column(name = "sent_at", nullable = false)
    val sentAt: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apply_user_id")
    var applyUser: ApplyUser? = null
)