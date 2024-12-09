package com.chat.homework.message.repository.custom

import com.chat.homework.message.application.dto.MessageSearchServiceRequest
import com.chat.homework.message.domain.Message

interface MessageRepositoryCustom {
    fun findLastMessagesByRoomId(roomId: Long, messageSearchServiceRequest: MessageSearchServiceRequest): List<Message>
}
