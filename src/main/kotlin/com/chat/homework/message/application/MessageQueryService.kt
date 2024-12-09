package com.chat.homework.message.application

import com.chat.homework.message.application.dto.MessageSearchServiceRequest
import com.chat.homework.message.application.dto.MessageServiceResponses

interface MessageQueryService {
    fun getMessages(roomId: Long, messageSearchServiceRequest: MessageSearchServiceRequest): MessageServiceResponses
}
