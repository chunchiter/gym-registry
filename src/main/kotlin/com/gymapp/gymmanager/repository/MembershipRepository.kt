package com.gymapp.gymmanager.repository

import com.gymapp.gymmanager.entity.Membership
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface MembershipRepository : JpaRepository<Membership, Long> {

    fun findByMemberId(memberId: Long): List<Membership>

    @Query("SELECT m FROM Membership m WHERE m.fechaVencimiento < :today")
    fun findExpired(today: LocalDate): List<Membership>

    @Query("SELECT m FROM Membership m WHERE m.fechaVencimiento BETWEEN :today AND :soon")
    fun findExpiringSoon(today: LocalDate, soon: LocalDate): List<Membership>
}