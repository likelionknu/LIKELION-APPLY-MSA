package com.likelionknu.applyserver.recruit.data.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "apply_recruit")
class Recruit(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var generation: Int,

    @Column(name = "start_at", nullable = false)
    var startAt: LocalDateTime,

    @Column(name = "end_at", nullable = false)
    var endAt: LocalDateTime,

    @Column(name = "document_result_at", nullable = false)
    var documentResultAt: LocalDateTime,

    @Column(name = "final_result_at", nullable = false)
    var finalResultAt: LocalDateTime,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null
) {
    fun update(
        title: String,
        generation: Int,
        startAt: LocalDateTime,
        endAt: LocalDateTime,
        documentResultAt: LocalDateTime,
        finalResultAt: LocalDateTime
    ) {
        this.title = title
        this.generation = generation
        this.startAt = startAt
        this.endAt = endAt
        this.documentResultAt = documentResultAt
        this.finalResultAt = finalResultAt
    }

    fun softDelete() {
        deletedAt = LocalDateTime.now()
    }
}