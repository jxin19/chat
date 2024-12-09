package com.chat.homework.message.ui.dto

import com.chat.homework.message.application.dto.MessageServiceResponse

data class MessageResponse(
    val senderMemberId: Long,
    val senderMemberName: String,
    val content: String,
    val createdAt: String,
) {
    companion object {
        fun of(message: MessageServiceResponse) = MessageResponse(
            senderMemberId = message.senderMemberId,
            senderMemberName = message.senderMemberName,
            content = message.content,
            createdAt = message.createdAt.toString(),
        )
    }
}
