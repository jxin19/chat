package com.chat.homework.member.ui.dto

import com.chat.homework.member.application.dto.MemberServiceResponse

data class MemberResponse(
    val id: Long,
    val name: String,
    val username: String
) {
    companion object {
        fun of(memberServiceResponse: MemberServiceResponse) = MemberResponse(
            id = memberServiceResponse.id,
            name = memberServiceResponse.name,
            username = memberServiceResponse.username
        )
    }
}
