package com.chat.homework.message.application.dto

import com.chat.homework.message.domain.Message
import java.time.LocalDateTime

class MessageServiceResponse(
    val id: String,
    val roomId: Long,
    val senderMemberId: Long,
    val senderMemberName: String,
    val content: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun of(message: Message) = MessageServiceResponse(
            id = message.id!!,
            roomId = message.roomId,
            senderMemberId = message.senderId,
            senderMemberName = message.senderName,
            content = message.contentValue,
            createdAt = message.createdAt,
        )
    }
}
