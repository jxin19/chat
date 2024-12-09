package com.chat.homework.member.application

import com.chat.homework.member.application.dto.MemberServiceRequest
import com.chat.homework.member.application.dto.MemberServiceResponse

interface MemberCommandService {
    fun create(memberServiceRequest: MemberServiceRequest): MemberServiceResponse
    fun update(id: Long, memberServiceRequest: MemberServiceRequest): MemberServiceResponse
}
