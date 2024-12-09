package com.chat.homework.room

import com.chat.homework.common.application.RedissonDistributedLockService
import com.chat.homework.room.application.RoomCommandService
import com.chat.homework.room.application.RoomQueryService
import com.chat.homework.room.application.dto.RoomServiceRequest
import com.chat.homework.room.application.impl.RoomCommandServiceImpl
import com.chat.homework.room.domain.Room
import com.chat.homework.room.domain.property.*
import com.chat.homework.room.repository.RoomRepository
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.SetOperations
import org.springframework.data.redis.core.StringRedisTemplate

class RoomCommandServiceTests {
    private lateinit var roomRepository: RoomRepository
    private lateinit var roomQueryService: RoomQueryService
    private lateinit var stringRedisTemplate: StringRedisTemplate
    private lateinit var distributedLockService: RedissonDistributedLockService
    private lateinit var roomCommandService: RoomCommandService
    private lateinit var setOperations: SetOperations<String, String>

    @BeforeEach
    fun setUp() {
        roomRepository = mockk()
        roomQueryService = mockk()
        stringRedisTemplate = mockk()
        distributedLockService = mockk()
        setOperations = mockk()
        roomCommandService = RoomCommandServiceImpl(
            roomRepository,
            roomQueryService,
            stringRedisTemplate,
            distributedLockService
        )
    }

    @Test
    fun `채팅방_생성_성공`() {
        // given
        val request = RoomServiceRequest(
            name = "개발자 모임방",
            roomType = RoomType.GROUP,
            maxParticipants = 10,
            status = RoomStatus.ACTIVE
        )

        val savedRoom = Room(
            id = 1L,
            name = RoomName("개발자 모임방"),
            roomType = RoomType.GROUP,
            maxParticipants = MaxParticipants(10),
            status = RoomStatus.ACTIVE
        )

        every { roomRepository.save(any()) } returns savedRoom

        // when
        val response = roomCommandService.create(request)

        // then
        assertThat(response.id).isEqualTo(1L)
        assertThat(response.name).isEqualTo("개발자 모임방")
        assertThat(response.roomType).isEqualTo("GROUP")
        assertThat(response.maxParticipants).isEqualTo(10)
        assertThat(response.status).isEqualTo("ACTIVE")
        verify { roomRepository.save(any()) }
    }

    @Test
    fun `채팅방_수정_성공`() {
        // given
        val roomId = 1L
        val request = RoomServiceRequest(
            name = "수정된 채팅방",
            roomType = RoomType.GROUP,
            maxParticipants = 20,
            status = RoomStatus.ACTIVE
        )

        val existingRoom = Room(
            id = roomId,
            name = RoomName("개발자 모임방"),
            roomType = RoomType.GROUP,
            maxParticipants = MaxParticipants(10),
            status = RoomStatus.ACTIVE
        )

        every { roomQueryService.fetchRoomById(roomId) } returns existingRoom

        // when
        val response = roomCommandService.update(roomId, request)

        // then
        assertThat(response.name).isEqualTo("수정된 채팅방")
        assertThat(response.maxParticipants).isEqualTo(20)
        verify { roomQueryService.fetchRoomById(roomId) }
    }

    @Test
    fun `채팅방_삭제_성공`() {
        // given
        val roomId = 1L
        val existingRoom = Room(
            id = roomId,
            name = RoomName("개발자 모임방"),
            roomType = RoomType.GROUP,
            maxParticipants = MaxParticipants(10),
            status = RoomStatus.ACTIVE
        )

        every { roomQueryService.fetchRoomById(roomId) } returns existingRoom

        // when
        roomCommandService.delete(roomId)

        // then
        assertThat(existingRoom.statusValue).isEqualTo("DELETED")
        verify { roomQueryService.fetchRoomById(roomId) }
    }

}
