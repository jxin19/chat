package com.chat.homework.message.ui.dto

import com.chat.homework.message.application.dto.MessageServiceResponses

data class MessageResponses(
    val list: List<MessageResponse>
) {
    companion object {
        fun of(messageServiceResponses: MessageServiceResponses) =
            MessageResponses(messageServiceResponses.list.map { MessageResponse.of(it) })
    }

}
