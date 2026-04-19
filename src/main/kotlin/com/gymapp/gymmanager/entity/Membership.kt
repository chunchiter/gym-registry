package com.gymapp.gymmanager.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "memberships")
data class Membership(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member = Member(),

    @Column(nullable = false)
    val fechaPago: LocalDate = LocalDate.now(),

    @Column(nullable = false)
    val fechaVencimiento: LocalDate = LocalDate.now(),

    @Column(nullable = false)
    val montoPagado: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    val metodoPago: String = "Efectivo"
)