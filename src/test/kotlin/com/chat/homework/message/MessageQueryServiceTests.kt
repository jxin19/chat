package com.chat.homework.message

import com.chat.homework.message.application.MessageQueryService
import com.chat.homework.message.application.dto.MessageSearchServiceRequest
import com.chat.homework.message.application.impl.MessageQueryServiceImpl
import com.chat.homework.message.domain.Message
import com.chat.homework.message.domain.property.MessageContent
import com.chat.homework.message.repository.MessageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class MessageQueryServiceTests {
    private lateinit var messageRepository: MessageRepository
    private lateinit var messageQueryService: MessageQueryService

    @BeforeEach
    fun setUp() {
        messageRepository = mockk()
        messageQueryService = MessageQueryServiceImpl(messageRepository)
    }

    @Test
    fun `채팅방_메시지_조회_성공`() {
        // given
        val roomId = 1L
        val searchRequest = MessageSearchServiceRequest(
            page = 0,
            size = 20
        )

        val now = LocalDateTime.now()
        val messages = listOf(
            Message(
                id = "message1",
                roomId = roomId,
                senderId = 100L,
                senderName = "홍길동",
                content = MessageContent("안녕하세요"),
                createdAt = now
            ),
            Message(
                id = "message2",
                roomId = roomId,
                senderId = 200L,
                senderName = "김철수",
                content = MessageContent("반갑습니다"),
                createdAt = now.plusMinutes(1)
            )
        )

        every { messageRepository.findLastMessagesByRoomId(roomId, searchRequest) } returns messages

        // when
        val response = messageQueryService.getMessages(roomId, searchRequest)

        // then
        assertThat(response.list).hasSize(2)

        val firstMessage = response.list[0]
        assertThat(firstMessage.id).isEqualTo("message1")
        assertThat(firstMessage.roomId).isEqualTo(roomId)
        assertThat(firstMessage.senderMemberId).isEqualTo(100L)
        assertThat(firstMessage.senderMemberName).isEqualTo("홍길동")
        assertThat(firstMessage.content).isEqualTo("안녕하세요")
        assertThat(firstMessage.createdAt).isEqualTo(now)

        val secondMessage = response.list[1]
        assertThat(secondMessage.id).isEqualTo("message2")
        assertThat(secondMessage.roomId).isEqualTo(roomId)
        assertThat(secondMessage.senderMemberId).isEqualTo(200L)
        assertThat(secondMessage.senderMemberName).isEqualTo("김철수")
        assertThat(secondMessage.content).isEqualTo("반갑습니다")
        assertThat(secondMessage.createdAt).isEqualTo(now.plusMinutes(1))

        verify { messageRepository.findLastMessagesByRoomId(roomId, searchRequest) }
    }

    @Test
    fun `채팅방_메시지가_없는_경우_빈_리스트_반환`() {
        // given
        val roomId = 1L
        val searchRequest = MessageSearchServiceRequest(
            page = 0,
            size = 20
        )

        every { messageRepository.findLastMessagesByRoomId(roomId, searchRequest) } returns emptyList()

        // when
        val response = messageQueryService.getMessages(roomId, searchRequest)

        // then
        assertThat(response.list).isEmpty()
        verify { messageRepository.findLastMessagesByRoomId(roomId, searchRequest) }
    }

    @Test
    fun `페이징_파라미터_전달_검증`() {
        // given
        val roomId = 1L
        val searchRequest = MessageSearchServiceRequest(
            page = 2,
            size = 10
        )

        every { messageRepository.findLastMessagesByRoomId(roomId, searchRequest) } returns emptyList()

        // when
        messageQueryService.getMessages(roomId, searchRequest)

        // then
        verify {
            messageRepository.findLastMessagesByRoomId(
                roomId,
                withArg { request ->
                    assertThat(request.page).isEqualTo(2)
                    assertThat(request.size).isEqualTo(10)
                }
            )
        }
    }
}
