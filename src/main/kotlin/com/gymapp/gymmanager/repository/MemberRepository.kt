package com.gymapp.gymmanager.repository

import com.gymapp.gymmanager.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long>