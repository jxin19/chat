package com.chat.homework.room.application.impl

import com.chat.homework.common.application.RedissonDistributedLockService
import com.chat.homework.room.application.RoomCommandService
import com.chat.homework.room.application.RoomQueryService
import com.chat.homework.room.application.dto.*
import com.chat.homework.room.application.extension.toDto
import com.chat.homework.room.application.extension.toRedisData
import com.chat.homework.room.repository.RoomRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.redis.core.StringRedisTemplate

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
class RoomCommandServiceImpl(
    private val roomRepository: RoomRepository,
    private val roomQueryService: RoomQueryService,
    private val stringRedisTemplate: StringRedisTemplate,
    private val distributedLockService: RedissonDistributedLockService
) : RoomCommandService {

    @CachePut(cacheNames = ["room"], key = "#result.id")
    override fun create(roomServiceRequest: RoomServiceRequest): RoomServiceResponse {
        val newRoom = roomServiceRequest.toEntity()

        val savedRoom = roomRepository.save(newRoom)

        return RoomServiceResponse.of(savedRoom)
    }

    @CacheEvict(cacheNames = ["room"], key = "#id")
    override fun update(id: Long, roomServiceRequest: RoomServiceRequest): RoomServiceResponse {
        val room = roomQueryService.fetchRoomById(id)
        val updateRoom = roomServiceRequest.toEntity()

        room.update(updateRoom)

        return RoomServiceResponse.of(room)
    }

    @CacheEvict(cacheNames = ["room"], key = "#id")
    override fun delete(id: Long) {
        val room = roomQueryService.fetchRoomById(id)
        room.updateDeletionStatus()
    }

    override fun handleEnter(
        id: Long,
        participantServiceRequest: ParticipantServiceRequest
    ): ParticipantServiceResponses =
        updateParticipantCount(id, 1, participantServiceRequest)

    override fun handleLeave(
        id: Long,
        participantServiceRequest: ParticipantServiceRequest
    ): ParticipantServiceResponses =
        updateParticipantCount(id, -1, participantServiceRequest)

    private fun updateParticipantCount(
        roomId: Long,
        delta: Int,
        participantServiceRequest: ParticipantServiceRequest
    ): ParticipantServiceResponses {
        val lockKey = "lock:room:$roomId:participants"
        val participantKey = "room:$roomId:participants"
        var participants = ParticipantServiceResponses()

        distributedLockService.executeLockOperation(lockKey, 3) {
            val data = participantServiceRequest.toRedisData()

            when (delta) {
                1 -> stringRedisTemplate.opsForSet().add(participantKey, data)
                -1 -> stringRedisTemplate.opsForSet().remove(participantKey, data)
            }

            participants = roomQueryService.getParticipants(roomId)
        }

        return participants
    }

}
