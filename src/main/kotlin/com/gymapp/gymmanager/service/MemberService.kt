package com.gymapp.gymmanager.service

import com.gymapp.gymmanager.entity.Member
import com.gymapp.gymmanager.repository.MemberRepository
import com.gymapp.gymmanager.repository.MembershipRepository
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val membershipRepository: MembershipRepository
) {

    fun getAll(): List<Map<String, Any?>> {
        return memberRepository.findAll().map { member ->
            val lastMembership = membershipRepository
                .findByMemberId(member.id!!)
                .maxByOrNull { it.fechaVencimiento }
            mapOf(
                "id" to member.id,
                "nombre" to member.nombre,
                "telefono" to member.telefono,
                "email" to member.email,
                "fechaRegistro" to member.fechaRegistro,
                "activo" to member.activo,
                "lastMembership" to lastMembership
            )
        }
    }

    fun getById(id: Long): Member = memberRepository.findById(id)
        .orElseThrow { RuntimeException("Miembro no encontrado") }

    fun create(member: Member): Member = memberRepository.save(member)

    fun update(id: Long, member: Member): Member {
        val existing = getById(id)
        return memberRepository.save(existing.copy(
            nombre = member.nombre,
            telefono = member.telefono,
            email = member.email,
            activo = member.activo
        ))
    }

    fun delete(id: Long) = memberRepository.deleteById(id)
}