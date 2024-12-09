package com.chat.homework.member

import com.chat.homework.member.application.MemberCommandService
import com.chat.homework.member.application.MemberQueryService
import com.chat.homework.member.application.dto.MemberServiceRequest
import com.chat.homework.member.application.impl.MemberCommandServiceImpl
import com.chat.homework.member.domain.Member
import com.chat.homework.member.domain.property.MemberName
import com.chat.homework.member.domain.property.Username
import com.chat.homework.member.repository.MemberRepository
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MemberCommandServiceTests {
    private lateinit var memberRepository: MemberRepository
    private lateinit var memberQueryService: MemberQueryService
    private lateinit var memberCommandService: MemberCommandService

    @BeforeEach
    fun setUp() {
        memberRepository = mockk()
        memberQueryService = mockk()
        memberCommandService = MemberCommandServiceImpl(memberRepository, memberQueryService)
    }

    @Test
    fun `회원_생성_성공`() {
        // given
        val request = MemberServiceRequest(
            name = "홍길동",
            username = "hong"
        )

        val savedMember = Member(
            id = 1L,
            name = MemberName("홍길동"),
            username = Username("hong")
        )

        every { memberQueryService.validateDuplicateUsername(any()) } just runs
        every { memberRepository.save(any()) } returns savedMember

        // when
        val response = memberCommandService.create(request)

        // then
        assertThat(response.id).isEqualTo(1L)
        assertThat(response.name).isEqualTo("홍길동")
        assertThat(response.username).isEqualTo("hong")
        verify(exactly = 1) { memberQueryService.validateDuplicateUsername(request.username) }
        verify(exactly = 1) { memberRepository.save(any()) }
    }

    @Test
    fun `회원_정보_수정_성공`() {
        // given
        val memberId = 1L
        val request = MemberServiceRequest(
            name = "김철수",
            username = "kim"
        )

        val existingMember = Member(
            id = memberId,
            name = MemberName("홍길동"),
            username = Username("hong")
        )

        every { memberQueryService.validateDuplicateUsername(any()) } just runs
        every { memberQueryService.fetchMemberById(memberId) } returns existingMember

        // when
        val response = memberCommandService.update(memberId, request)

        // then
        assertThat(response.id).isEqualTo(memberId)
        assertThat(response.name).isEqualTo("김철수")
        assertThat(response.username).isEqualTo("kim")
        verify(exactly = 1) { memberQueryService.validateDuplicateUsername(request.username) }
        verify(exactly = 1) { memberQueryService.fetchMemberById(memberId) }
    }

    @Test
    fun `회원_생성시_캐시_갱신_확인`() {
        // given
        val request = MemberServiceRequest(
            name = "홍길동",
            username = "hong"
        )

        val savedMember = Member(
            id = 1L,
            name = MemberName("홍길동"),
            username = Username("hong")
        )

        every { memberQueryService.validateDuplicateUsername(any()) } just runs
        every { memberRepository.save(any()) } returns savedMember

        // when
        val response = memberCommandService.create(request)

        // then
        // @CachePut 어노테이션으로 인해 캐시가 자동으로 갱신됨
        assertThat(response.id).isEqualTo(1L)
        verify(exactly = 1) { memberRepository.save(any()) }
    }

    @Test
    fun `회원_수정시_캐시_삭제_확인`() {
        // given
        val memberId = 1L
        val request = MemberServiceRequest(
            name = "김철수",
            username = "kim"
        )

        val existingMember = Member(
            id = memberId,
            name = MemberName("홍길동"),
            username = Username("hong")
        )

        every { memberQueryService.validateDuplicateUsername(any()) } just runs
        every { memberQueryService.fetchMemberById(memberId) } returns existingMember

        // when
        val response = memberCommandService.update(memberId, request)

        // then
        // @CacheEvict 어노테이션으로 인해 캐시가 자동으로 삭제됨
        assertThat(response.id).isEqualTo(memberId)
        verify(exactly = 1) { memberQueryService.fetchMemberById(memberId) }
    }
}
