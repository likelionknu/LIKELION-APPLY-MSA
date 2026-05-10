package com.likelionknu.applyserver.application.data.entity

import com.likelionknu.applyserver.auth.data.entity.User
import com.likelionknu.applyserver.auth.data.enums.ApplicationEvaluation
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.recruit.data.entity.Recruit
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "application")
class Application(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id", nullable = false)
    val recruit: Recruit,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @OneToMany(mappedBy = "application", cascade = [CascadeType.ALL], orphanRemoval = true)
    var answers: MutableList<RecruitAnswer?> = ArrayList(),

    @Column(name = "note", length = 100)
    var note: String? = null,

    @Enumerated(EnumType.STRING)
    var evaluation: ApplicationEvaluation? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ApplicationStatus? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "before_canceled_status")
    var beforeCanceledStatus: ApplicationStatus? = null,

    @Column(name = "submitted_at", nullable = false)
    var submittedAt: LocalDateTime
) {
    fun updateNote(memo: String?) {
        this.note = memo
    }

    fun updateEvaluation(evaluation: ApplicationEvaluation?) {
        this.evaluation = evaluation
    }

    fun changeStatus(newStatus: ApplicationStatus?) {
        if (newStatus == null) return

        if (newStatus == ApplicationStatus.CANCELED && this.status != ApplicationStatus.CANCELED) {
            this.beforeCanceledStatus = this.status
            this.status = ApplicationStatus.CANCELED
            resetEvaluation()
            return
        }

        if (this.status == ApplicationStatus.CANCELED && newStatus != ApplicationStatus.CANCELED) {
            this.status = newStatus
            this.beforeCanceledStatus = null
            resetEvaluation()
            return
        }

        if (this.status != newStatus) {
            this.status = newStatus
            resetEvaluation()
        }
    }

    fun restoreFromCanceled() {
        check(this.status == ApplicationStatus.CANCELED) { "CANCELED 상태가 아닙니다." }

        checkNotNull(this.beforeCanceledStatus) { "복구할 이전 상태 정보가 없습니다." }

        this.status = this.beforeCanceledStatus
        this.beforeCanceledStatus = null
        resetEvaluation()
    }

    private fun resetEvaluation() {
        this.evaluation = null
    }
}