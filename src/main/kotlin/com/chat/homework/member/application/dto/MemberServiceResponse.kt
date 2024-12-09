package com.chat.homework.member.application.dto

import com.chat.homework.member.domain.Member

data class MemberServiceResponse(
    val id: Long = 0,
    val name: String = "",
    val username: String = ""
) {
    companion object {
        fun of(member: Member) = MemberServiceResponse(
            id = member.id,
            name = member.nameValue,
            username = member.usernameValue
        )
    }
}
