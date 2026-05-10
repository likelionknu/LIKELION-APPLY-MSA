package com.likelionknu.applyserver.auth.data.entity

import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.auth.data.enums.Role
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "user")
class User (
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

        @OneToOne(mappedBy = "user",
                cascade = [CascadeType.ALL],
                orphanRemoval = true,
                fetch = FetchType.LAZY)
        var profile: Profile?= null,

        // 사용자 삭제 시 함께 삭제되도록 하기 위한 연관관계
        @OneToMany(mappedBy = "user",
                cascade = [CascadeType.ALL],
                orphanRemoval = true)
        val applications: MutableList<Application> = mutableListOf(),

        @UpdateTimestamp
        @Column(name = "modified_at", nullable = false)
        var modifiedAt: LocalDateTime? = null,

        @Column(name = "last_access_at", nullable = false)
        var lastAccessAt: LocalDateTime? = null,

        @CreationTimestamp
        @Column(name = "registered_at", nullable = false, updatable = false)
        var registeredAt: LocalDateTime? = null
)