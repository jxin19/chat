package com.chat.homework.member.application.impl

import com.chat.homework.member.application.MemberCommandService
import com.chat.homework.member.application.MemberQueryService
import com.chat.homework.member.application.dto.MemberServiceRequest
import com.chat.homework.member.application.dto.MemberServiceResponse
import com.chat.homework.member.repository.MemberRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
class MemberCommandServiceImpl(
    private val memberRepository: MemberRepository,
    private val memberQueryService: MemberQueryService
) : MemberCommandService {

    @CachePut(cacheNames = ["member"], key = "#result.id")
    override fun create(memberServiceRequest: MemberServiceRequest): MemberServiceResponse {
        val newMember = memberServiceRequest.toEntity()

        memberQueryService.validateDuplicateUsername(memberServiceRequest.username)

        val savedMember = memberRepository.save(newMember)

        return MemberServiceResponse.of(savedMember)
    }

    @CacheEvict(cacheNames = ["member"], key = "#id")
    override fun update(id: Long, memberServiceRequest: MemberServiceRequest): MemberServiceResponse {
        val member = memberQueryService.fetchMemberById(id)

        memberQueryService.validateDuplicateUsername(memberServiceRequest.username)

        val updateMember = memberServiceRequest.toEntity()

        member.update(updateMember)

        return MemberServiceResponse.of(member)
    }

}
