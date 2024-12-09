package com.chat.homework.room.application.dto

import com.chat.homework.room.domain.Room

data class RoomServiceResponse(
    val id: Long = 0,
    val name: String = "",
    val roomType: String = "",
    val maxParticipants: Int = 0,
    val status: String = "",
    val participants: Int = 0
) {
    companion object {
        fun of(room: Room, participants: Int = 0) = RoomServiceResponse(
            id = room.id,
            name = room.roomNameValue,
            roomType = room.roomTypeValue,
            maxParticipants = room.maxParticipantsValue,
            status = room.statusValue,
            participants = participants
        )
    }
}
