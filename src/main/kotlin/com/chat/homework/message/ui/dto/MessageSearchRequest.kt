package com.chat.homework.message.ui.dto

import com.chat.homework.message.application.dto.MessageSearchServiceRequest

data class MessageSearchRequest(
    val page: Int = 1,
    val size: Int = 20
) {
    fun toServiceDto() = MessageSearchServiceRequest(
        page = page,
        size = size
    )
}
