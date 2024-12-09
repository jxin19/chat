package com.chat.homework.room.application

import com.chat.homework.room.application.dto.ParticipantServiceResponses
import com.chat.homework.room.application.dto.RoomServiceResponse
import com.chat.homework.room.application.dto.RoomServiceResponses
import com.chat.homework.room.domain.Room

interface RoomQueryService {
    fun getList(): RoomServiceResponses
    fun getRoomById(id: Long): RoomServiceResponse
    fun fetchRoomById(id: Long): Room
    fun getParticipants(roomId: Long): ParticipantServiceResponses
}
