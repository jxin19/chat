package com.chat.homework.member.application.dto

import com.chat.homework.member.domain.Member
import com.chat.homework.member.domain.property.MemberName
import com.chat.homework.member.domain.property.Username

data class MemberServiceRequest(
    val name: String,
    val username: String
) {
    fun toEntity() = Member(
        name = MemberName(name),
        username = Username(username)
    )
}
