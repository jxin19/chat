package com.chat.homework.member.application.impl

import com.chat.homework.member.application.MemberQueryService
import com.chat.homework.member.application.dto.MemberServiceResponse
import com.chat.homework.member.domain.Member
import com.chat.homework.member.domain.property.Username
import com.chat.homework.member.repository.MemberRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberQueryServiceImpl(
    private val memberRepository: MemberRepository,
) : MemberQueryService {

    override fun validateDuplicateUsername(username: String) {
        require(!memberRepository.existsByUsername__value(username)) {
            "이미 존재하는 유저네임입니다. - $username"
        }
    }

    @Cacheable(cacheNames = ["member"], key = "#id")
    override fun getMemberById(id: Long) =
        MemberServiceResponse.of(fetchMemberById(id))

    override fun fetchMemberById(id: Long): Member =
        memberRepository.findById(id)
            .orElseThrow { NoSuchElementException("존재하지 않는 유저입니다. - $id") }

}
