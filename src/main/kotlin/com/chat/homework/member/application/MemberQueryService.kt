package com.chat.homework.member.application

import com.chat.homework.member.application.dto.MemberServiceResponse
import com.chat.homework.member.domain.Member

interface MemberQueryService {
    fun validateDuplicateUsername(username: String)
    fun getMemberById(id: Long): MemberServiceResponse
    fun fetchMemberById(id: Long): Member
}
