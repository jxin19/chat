package com.chat.homework.message.ui.dto

import com.chat.homework.message.application.dto.MessageServiceRequest

data class MessageRequest(
    val roomId: Long,
    val senderMemberId: Long,
    val senderMemberName: String,
    val content: String
) {
    fun toServiceDto() = MessageServiceRequest(
        roomId = roomId,
        senderMemberId = senderMemberId,
        senderMemberName = senderMemberName,
        content = content,
    )
}
