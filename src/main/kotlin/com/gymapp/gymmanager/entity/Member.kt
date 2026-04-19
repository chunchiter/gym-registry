package com.gymapp.gymmanager.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "members")
data class Member(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val nombre: String = "",

    @Column(nullable = false)
    val telefono: String = "",

    @Column(nullable = false)
    val email: String = "",

    @Column(nullable = false)
    val fechaRegistro: LocalDate = LocalDate.now(),

    @Column(nullable = false)
    val activo: Boolean = true
)