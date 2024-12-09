package com.chat.homework.room.application.dto

import com.chat.homework.room.domain.Room
import com.chat.homework.room.domain.property.MaxParticipants
import com.chat.homework.room.domain.property.RoomName
import com.chat.homework.room.domain.property.RoomStatus
import com.chat.homework.room.domain.property.RoomType

data class RoomServiceRequest(
    val name: String,
    val roomType: RoomType,
    val maxParticipants: Int,
    val status: RoomStatus,
) {
    fun toEntity() = Room(
        name = RoomName(name),
        roomType = roomType,
        maxParticipants = MaxParticipants(maxParticipants),
        status = status,
    )
}
