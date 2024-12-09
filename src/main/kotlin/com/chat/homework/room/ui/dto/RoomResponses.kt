package com.chat.homework.room.ui.dto

import com.chat.homework.room.application.dto.RoomServiceResponses

data class RoomResponses(
    val list: List<RoomResponse>
) {
    companion object {
        fun of(roomServiceResponses: RoomServiceResponses) =
            RoomResponses(roomServiceResponses.list.map { RoomResponse.of(it) })
    }
}
