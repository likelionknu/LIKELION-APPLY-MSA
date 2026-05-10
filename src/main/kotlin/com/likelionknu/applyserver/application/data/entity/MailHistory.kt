package com.likelionknu.applyserver.application.data.entity

import com.likelionknu.applyserver.auth.data.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "mail_history")
class MailHistory(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    val subject: String,

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    val body: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null,

    @Column(nullable = false)
    val recipient: String,

    @Column(name = "sent_at", nullable = false)
    val sentAt: LocalDateTime
)