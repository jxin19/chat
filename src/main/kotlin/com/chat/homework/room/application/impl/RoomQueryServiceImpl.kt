package com.chat.homework.room.application.impl

import com.chat.homework.room.application.RoomQueryService
import com.chat.homework.room.application.dto.ParticipantServiceResponse
import com.chat.homework.room.application.dto.ParticipantServiceResponses
import com.chat.homework.room.application.dto.RoomServiceResponse
import com.chat.homework.room.application.dto.RoomServiceResponses
import com.chat.homework.room.domain.Room
import com.chat.homework.room.domain.property.RoomStatus
import com.chat.homework.room.repository.RoomRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RoomQueryServiceImpl(
    private val roomRepository: RoomRepository,
    private val redisTemplate: RedisTemplate<String, Any>
) : RoomQueryService {

    override fun getList(): RoomServiceResponses {
        val list = roomRepository.findRoomsByStatusIs(RoomStatus.ACTIVE)

        return RoomServiceResponses.of(
            list.map { RoomServiceResponse.of(it, getParticipants(it.id).size) }
        )
    }

    @Cacheable(cacheNames = ["room"], key = "#id")
    override fun getRoomById(id: Long): RoomServiceResponse {
        val room = fetchRoomById(id)
        return RoomServiceResponse.of(room, getParticipants(id).size)
    }

    override fun fetchRoomById(id: Long): Room =
        roomRepository.findById(id)
            .orElseThrow { NoSuchElementException("존재하지 않는 채팅방입니다. - $id") }

    override fun getParticipants(roomId: Long): ParticipantServiceResponses {
        val participantKey = "room:$roomId:participants"
        val participants: Set<ParticipantServiceResponse> = redisTemplate.opsForSet()
            .members(participantKey)
            ?.map { value ->
                when (value) {
                    is ParticipantServiceResponse -> value
                    is Map<*, *> -> ParticipantServiceResponse(
                        participantMemberId = (value["participantMemberId"] as Number).toLong(),
                        participantMemberName = value["participantMemberName"] as String
                    )
                    else -> {
                        val objectMapper = ObjectMapper()
                            .registerModule(JavaTimeModule())
                            .registerModule(KotlinModule.Builder().build())
                        objectMapper.convertValue(value, ParticipantServiceResponse::class.java)
                    }
                }
            }
            ?.toSet()
            ?: emptySet()

        return ParticipantServiceResponses.of(participants)
    }

}
