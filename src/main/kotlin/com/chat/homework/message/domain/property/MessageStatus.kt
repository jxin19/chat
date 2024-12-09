package com.chat.homework.message.domain.property

enum class MessageStatus {
    SENT,
    DELIVERED;

    companion object {
        @JvmStatic
        fun fromValue(value: String): MessageStatus =
            MessageStatus.entries.find { it.name == value } ?: throw IllegalArgumentException("잘못된 메시지 상태입니다. - $value")
    }
}
