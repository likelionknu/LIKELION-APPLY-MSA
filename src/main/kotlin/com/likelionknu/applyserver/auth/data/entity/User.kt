package com.likelionknu.applyserver.auth.data.entity

import com.likelionknu.applyserver.auth.data.enums.Role
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "user")
class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(nullable = false, unique = true)
        var email: String = "",

        @Column(nullable = false)
        var name: String = "",

        @Column(name = "profile_url", nullable = false)
        var profileUrl: String = "",

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        var role: Role = Role.USER,

        @OneToOne(
                mappedBy = "user",
                cascade = [CascadeType.ALL],
                orphanRemoval = true,
                fetch = FetchType.LAZY
        )
        var profile: Profile? = null,

        @UpdateTimestamp
        @Column(name = "modified_at", nullable = false)
        var modifiedAt: LocalDateTime? = null,

        @Column(name = "last_access_at", nullable = false)
        var lastAccessAt: LocalDateTime? = null,

        @CreationTimestamp
        @Column(name = "registered_at", nullable = false, updatable = false)
        var registeredAt: LocalDateTime? = null
)