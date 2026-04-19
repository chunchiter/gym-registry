package com.gymapp.gymmanager.dto

import java.math.BigDecimal
import java.time.LocalDate

data class MembershipRequest(
    val memberId: Long,
    val fechaPago: LocalDate,
    val fechaVencimiento: LocalDate,
    val montoPagado: BigDecimal,
    val metodoPago: String = "Efectivo"
)