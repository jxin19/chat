package com.chat.homework.room.application.dto

data class RoomServiceResponses(
    val list: List<RoomServiceResponse>
) {
    companion object {
        fun of(roomServiceResponses: List<RoomServiceResponse>) =
            RoomServiceResponses(roomServiceResponses)
    }
}
