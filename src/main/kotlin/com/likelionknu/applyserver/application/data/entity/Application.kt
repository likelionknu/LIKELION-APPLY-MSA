package com.likelionknu.applyserver.application.data.entity

import com.likelionknu.applyserver.application.data.enums.ApplicantPart
import com.likelionknu.applyserver.auth.data.enums.ApplicationEvaluation
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.recruit.data.entity.Recruit
import com.likelionknu.applyserver.user.data.entity.ApplyUser
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "apply_application",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_application_per_user",
            columnNames = ["recruit_id", "user_id"]
        )
    ]
)
class Application(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id", nullable = false)
    val recruit: Recruit,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: ApplyUser,

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_part", nullable = false)
    var preferredPart: ApplicantPart,

    @OneToMany(
        mappedBy = "application",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var answers: MutableList<RecruitAnswer?> = ArrayList(),

    @Column
    var note: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "document_evaluation")
    var documentEvaluation: ApplicationEvaluation? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_evaluation")
    var interviewEvaluation: ApplicationEvaluation? = null,

    @Enumerated(EnumType.STRING)
    @Column
    var evaluation: ApplicationEvaluation? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ApplicationStatus,

    @Enumerated(EnumType.STRING)
    @Column(name = "before_canceled_status")
    var beforeCanceledStatus: ApplicationStatus? = null,

    @Column(name = "submitted_at", nullable = false)
    var submittedAt: LocalDateTime
) {
    fun updatePreferredPart(preferredPart: ApplicantPart) {
        this.preferredPart = preferredPart
    }

    fun updateNote(memo: String?) {
        note = memo
    }

    fun updateDocumentEvaluation(
        evaluation: ApplicationEvaluation?
    ) {
        documentEvaluation = evaluation
    }

    fun updateInterviewEvaluation(
        evaluation: ApplicationEvaluation?
    ) {
        interviewEvaluation = evaluation
    }

    fun updateEvaluation(
        evaluation: ApplicationEvaluation?
    ) {
        this.evaluation = evaluation
    }

    fun changeStatus(newStatus: ApplicationStatus?) {
        if (newStatus == null || status == newStatus) {
            return
        }

        if (newStatus == ApplicationStatus.CANCELED) {
            beforeCanceledStatus = status
            status = ApplicationStatus.CANCELED
            return
        }

        status = newStatus
        beforeCanceledStatus = null
    }

    fun restoreFromCanceled() {
        check(status == ApplicationStatus.CANCELED) {
            "CANCELED 상태가 아닙니다."
        }

        status = checkNotNull(beforeCanceledStatus) {
            "복구할 이전 상태 정보가 없습니다."
        }

        beforeCanceledStatus = null
    }
}