package com.chat.homework.room.domain.property

enum class RoomType {
    ONE_TO_ONE,
    GROUP;

    companion object {
        @JvmStatic
        fun fromValue(value: String): RoomType =
            entries.find { it.name == value } ?: throw IllegalArgumentException("잘못된 채팅방 유형입니다. - $value")
    }
}
