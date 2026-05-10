package com.likelionknu.applyserver.recruit.data.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "recruit_content")
open class RecruitContent(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id", nullable = false)
    open var recruit: Recruit? = null,

    @Column(nullable = false)
    open var question: String = "",

    @Column(nullable = false)
    open var priority: Int = 0,

    @Column(nullable = false)
    open var required: Boolean = false
)