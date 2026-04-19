package com.gymapp.gymmanager.controller

import com.gymapp.gymmanager.dto.MemberRequest
import com.gymapp.gymmanager.entity.Member
import com.gymapp.gymmanager.service.MemberService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/members")
class MemberController(private val memberService: MemberService) {

    @GetMapping
    fun getAll() = ResponseEntity.ok(memberService.getAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = ResponseEntity.ok(memberService.getById(id))

    @PostMapping
    fun create(@RequestBody request: MemberRequest): ResponseEntity<Member> {
        val member = Member(
            nombre = request.nombre,
            telefono = request.telefono,
            email = request.email,
            activo = request.activo ?: true,
            fechaRegistro = LocalDate.now()
        )
        return ResponseEntity.ok(memberService.create(member))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: MemberRequest) =
        ResponseEntity.ok(memberService.update(id, Member(
            nombre = request.nombre,
            telefono = request.telefono,
            email = request.email,
            activo = request.activo ?: true
        )))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        memberService.delete(id)
        return ResponseEntity.noContent().build()
    }
}