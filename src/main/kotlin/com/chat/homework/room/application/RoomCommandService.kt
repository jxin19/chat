package com.chat.homework.room.application

import com.chat.homework.room.application.dto.ParticipantServiceRequest
import com.chat.homework.room.application.dto.ParticipantServiceResponses
import com.chat.homework.room.application.dto.RoomServiceRequest
import com.chat.homework.room.application.dto.RoomServiceResponse

interface RoomCommandService {
    fun create(roomServiceRequest: RoomServiceRequest): RoomServiceResponse
    fun update(id: Long, roomServiceRequest: RoomServiceRequest): RoomServiceResponse
    fun delete(id: Long)
    fun handleEnter(id: Long, participantServiceRequest: ParticipantServiceRequest): ParticipantServiceResponses
    fun handleLeave(id: Long, participantServiceRequest: ParticipantServiceRequest): ParticipantServiceResponses
}
