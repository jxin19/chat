package com.chat.homework.member.repository

import com.chat.homework.member.domain.Member
import org.springframework.data.repository.CrudRepository

interface MemberRepository : CrudRepository<Member, Long> {
    fun existsByUsername__value(username: String): Boolean
}
