package com.gymapp.gymmanager.service

import com.gymapp.gymmanager.entity.Member
import com.gymapp.gymmanager.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class MemberService(private val memberRepository: MemberRepository) {

    fun getAll(): List<Member> = memberRepository.findAll()

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