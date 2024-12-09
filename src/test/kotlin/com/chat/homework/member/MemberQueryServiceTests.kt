package com.chat.homework.member

import com.chat.homework.member.application.MemberQueryService
import com.chat.homework.member.application.impl.MemberQueryServiceImpl
import com.chat.homework.member.domain.Member
import com.chat.homework.member.domain.property.MemberName
import com.chat.homework.member.domain.property.Username
import com.chat.homework.member.repository.MemberRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class MemberQueryServiceTests {
    private lateinit var memberRepository: MemberRepository
    private lateinit var memberQueryService: MemberQueryService

    @BeforeEach
    fun setUp() {
        memberRepository = mockk()
        memberQueryService = MemberQueryServiceImpl(memberRepository)
    }

    @Test
    fun `회원_조회_성공`() {
        // given
        val memberId = 1L
        val member = Member(
            id = memberId,
            name = MemberName("홍길동"),
            username = Username("hong")
        )

        every { memberRepository.findById(memberId) } returns Optional.of(member)

        // when
        val response = memberQueryService.getMemberById(memberId)

        // then
        assertThat(response.id).isEqualTo(memberId)
        assertThat(response.name).isEqualTo("홍길동")
        assertThat(response.username).isEqualTo("hong")
        verify(exactly = 1) { memberRepository.findById(memberId) }
    }

    @Test
    fun `존재하지_않는_회원_조회시_예외발생`() {
        // given
        val nonExistentId = 999L
        every { memberRepository.findById(nonExistentId) } returns Optional.empty()

        // when & then
        assertThatThrownBy { memberQueryService.fetchMemberById(nonExistentId) }
            .isInstanceOf(NoSuchElementException::class.java)
            .hasMessage("존재하지 않는 유저입니다. - $nonExistentId")
    }

    @Test
    fun `중복되지_않은_유저네임_검증_성공`() {
        // given
        val username = "newuser"
        every { memberRepository.existsByUsername__value(username) } returns false

        // when & then
        memberQueryService.validateDuplicateUsername(username) // 예외가 발생하지 않아야 함
        verify { memberRepository.existsByUsername__value(username) }
    }

    @Test
    fun `중복된_유저네임_검증시_예외발생`() {
        // given
        val duplicateUsername = "existing"
        every { memberRepository.existsByUsername__value(duplicateUsername) } returns true

        // when & then
        assertThatThrownBy { memberQueryService.validateDuplicateUsername(duplicateUsername) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("이미 존재하는 유저네임입니다. - $duplicateUsername")
    }

}
