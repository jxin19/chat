package com.chat.homework.member.ui.dto

import com.chat.homework.member.application.dto.MemberServiceRequest
import jakarta.validation.constraints.NotBlank

data class MemberRequest(
    @field:NotBlank(message = "이름을 입력해주세요.")
    val name: String,

    @field:NotBlank(message = "유저네임을 입력해주세요.")
    val username: String
) {
    fun toServiceDto() = MemberServiceRequest(
        name = name,
        username = username,
    )
}
