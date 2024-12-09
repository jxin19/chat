package com.chat.homework.room.domain.property

enum class RoomStatus {
    ACTIVE,
    DELETED;

    companion object {
        @JvmStatic
        fun fromValue(value: String): RoomStatus =
            RoomStatus.entries.find { it.name == value } ?: throw IllegalArgumentException("잘못된 채팅방 상태입니다. - $value")
    }
}
