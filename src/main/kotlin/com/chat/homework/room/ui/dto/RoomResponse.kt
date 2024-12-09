package com.chat.homework.room.ui.dto

import com.chat.homework.room.application.dto.RoomServiceResponse

data class RoomResponse(
    val id: Long,
    val name: String,
    val roomType: String,
    val maxParticipants: Int,
    val status: String,
    val participants: Int
) {
    companion object {
        fun of(roomServiceResponse: RoomServiceResponse) = RoomResponse(
            id = roomServiceResponse.id,
            name = roomServiceResponse.name,
            roomType = roomServiceResponse.roomType,
            maxParticipants = roomServiceResponse.maxParticipants,
            status = roomServiceResponse.status,
            participants = roomServiceResponse.participants
        )}
}
