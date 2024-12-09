package com.chat.homework.message.application.dto

import com.chat.homework.message.domain.Message

data class MessageServiceResponses(
    val list: List<MessageServiceResponse>
) {
    companion object {
        fun of(messages: List<Message>) =
            MessageServiceResponses(messages.map { MessageServiceResponse.of(it) })
    }
}
