package com.chat.homework.message.application

import com.chat.homework.message.application.dto.MessageServiceRequest

interface MessageCommandService {
    fun storeMessage(messageServiceRequest: MessageServiceRequest)
}
