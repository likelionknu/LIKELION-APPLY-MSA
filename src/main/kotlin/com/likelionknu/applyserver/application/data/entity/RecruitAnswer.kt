package com.likelionknu.applyserver.application.data.entity

import com.likelionknu.applyserver.recruit.data.entity.RecruitContent
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

@Entity
@Table(name = "recruit_answer")
class RecruitAnswer(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    var application: Application,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    var content: RecruitContent,

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    var answer: String
) {
    fun updateAnswer(answer: String) {
        this.answer = answer
    }
}