package com.likelionknu.applyserver.auth.data.entity

import com.likelionknu.applyserver.auth.data.enums.StudentStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "profile")
open class Profile(

    @Id
    @Column(name = "user_id")
    open var userId: Long? = null,

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    open var user: User? = null,

    @Column(name = "student_id", unique = true)
    open var studentId: String? = null,

    @Column
    open var depart: String? = null,

    @Column
    open var phone: String? = null,

    @Column
    open var grade: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column
    open var status: StudentStatus? = null
)