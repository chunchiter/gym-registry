package com.gymapp.gymmanager.service

import com.gymapp.gymmanager.entity.Membership
import com.gymapp.gymmanager.repository.MembershipRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MembershipService(private val membershipRepository: MembershipRepository) {

    fun getAll(): List<Membership> = membershipRepository.findAll()

    fun getByMemberId(memberId: Long): List<Membership> =
        membershipRepository.findByMemberId(memberId)

    fun create(membership: Membership): Membership = membershipRepository.save(membership)

    fun getExpired(): List<Membership> =
        membershipRepository.findExpired(LocalDate.now())

    fun getExpiringSoon(days: Long = 5): List<Membership> =
        membershipRepository.findExpiringSoon(LocalDate.now(), LocalDate.now().plusDays(days))
}