package com.chat.homework.member

import com.chat.homework.member.domain.Member
import com.chat.homework.member.domain.property.MemberName
import com.chat.homework.member.domain.property.Username
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class MemberTests {

    @Test
    fun `멤버_생성_성공`() {
        // given
        val memberName = MemberName("홍길동")
        val username = Username("hong")

        // when
        val member = Member(name = memberName, username = username)

        // then
        assertThat(member.nameValue).isEqualTo("홍길동")
        assertThat(member.usernameValue).isEqualTo("hong")
    }

    @Test
    fun `멤버_정보_수정_성공`() {
        // given
        val member = Member(
            name = MemberName("홍길동"),
            username = Username("hong")
        )
        val updatedMember = Member(
            name = MemberName("김철수"),
            username = Username("kim")
        )

        // when
        member.update(updatedMember)

        // then
        assertThat(member.nameValue).isEqualTo("김철수")
        assertThat(member.usernameValue).isEqualTo("kim")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " "])
    fun `이름이_빈값이면_예외발생`(emptyName: String) {
        assertThatThrownBy { MemberName(emptyName) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("이름을 입력해주세요.")
    }

    @Test
    fun `이름이_100자_초과하면_예외발생`() {
        // given
        val longName = "a".repeat(101)

        // when & then
        assertThatThrownBy { MemberName(longName) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("이름은 1~100 이내로 입력해 주세요.")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " "])
    fun `유저네임이_빈값이면_예외발생`(emptyUsername: String) {
        assertThatThrownBy { Username(emptyUsername) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("유저네임을 입력해주세요.")
    }

    @Test
    fun `유저네임이_100자_초과하면_예외발생`() {
        // given
        val longUsername = "a".repeat(101)

        // when & then
        assertThatThrownBy { Username(longUsername) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("유저네임은 1~100 이내로 입력해 주세요.")
    }

    @Test
    fun `동일한_멤버_비교시_true_반환`() {
        // given
        val member1 = Member(
            id = 1L,
            name = MemberName("홍길동"),
            username = Username("hong")
        )
        val member2 = Member(
            id = 1L,
            name = MemberName("홍길동"),
            username = Username("hong")
        )

        // when & then
        assertThat(member1).isEqualTo(member2)
    }

    @Test
    fun `다른_멤버_비교시_false_반환`() {
        // given
        val member1 = Member(
            id = 1L,
            name = MemberName("홍길동"),
            username = Username("hong")
        )
        val member2 = Member(
            id = 2L,
            name = MemberName("김철수"),
            username = Username("kim")
        )

        // when & then
        assertThat(member1).isNotEqualTo(member2)
    }
}
