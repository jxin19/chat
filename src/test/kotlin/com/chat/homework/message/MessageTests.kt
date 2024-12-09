package com.chat.homework.message

import com.chat.homework.message.domain.Message
import com.chat.homework.message.domain.property.MessageContent
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDateTime

class MessageTests {

    @Test
    fun `메시지_생성_성공`() {
        // given
        val roomId = 1L
        val senderId = 100L
        val senderName = "홍길동"
        val content = MessageContent("안녕하세요")
        val now = LocalDateTime.now()

        // when
        val message = Message(
            roomId = roomId,
            senderId = senderId,
            senderName = senderName,
            content = content,
            createdAt = now
        )

        // then
        assertThat(message.roomId).isEqualTo(roomId)
        assertThat(message.senderId).isEqualTo(senderId)
        assertThat(message.senderName).isEqualTo(senderName)
        assertThat(message.contentValue).isEqualTo("안녕하세요")
        assertThat(message.createdAt).isEqualTo(now)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " "])
    fun `메시지_내용이_빈값이면_예외발생`(emptyContent: String) {
        assertThatThrownBy { MessageContent(emptyContent) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("메시지 내용을 입력해주세요.")
    }

    @Test
    fun `메시지_내용이_1000자_초과하면_예외발생`() {
        // given
        val longContent = "a".repeat(1001)

        // when & then
        assertThatThrownBy { MessageContent(longContent) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("메시지는 1~1000 이내로 입력해 주세요.")
    }

    @Test
    fun `동일한_메시지_비교시_true_반환`() {
        // given
        val now = LocalDateTime.now()
        val message1 = Message(
            id = "message1",
            roomId = 1L,
            senderId = 100L,
            senderName = "홍길동",
            content = MessageContent("안녕하세요"),
            createdAt = now
        )
        val message2 = Message(
            id = "message1",
            roomId = 1L,
            senderId = 100L,
            senderName = "홍길동",
            content = MessageContent("안녕하세요"),
            createdAt = now
        )

        // when & then
        assertThat(message1).isEqualTo(message2)
    }

    @Test
    fun `다른_메시지_비교시_false_반환`() {
        // given
        val message1 = Message(
            id = "message1",
            roomId = 1L,
            senderId = 100L,
            senderName = "홍길동",
            content = MessageContent("안녕하세요"),
            createdAt = LocalDateTime.now()
        )
        val message2 = Message(
            id = "message2",
            roomId = 2L,
            senderId = 200L,
            senderName = "김철수",
            content = MessageContent("반갑습니다"),
            createdAt = LocalDateTime.now()
        )

        // when & then
        assertThat(message1).isNotEqualTo(message2)
    }

    @Test
    fun `메시지_내용_앞뒤_공백_제거_확인`() {
        // given
        val contentWithSpaces = "  안녕하세요  "

        // when
        val messageContent = MessageContent(contentWithSpaces)

        // then
        assertThat(messageContent.value).isEqualTo("안녕하세요")
    }
}
