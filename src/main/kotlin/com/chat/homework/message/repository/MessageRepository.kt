package com.chat.homework.message.repository

import com.chat.homework.message.application.dto.MessageSearchServiceRequest
import com.chat.homework.message.domain.Message
import com.chat.homework.message.repository.custom.MessageRepositoryCustom
import org.springframework.data.mongodb.repository.MongoRepository

interface MessageRepository : MongoRepository<Message, Long>, MessageRepositoryCustom {
    override fun findLastMessagesByRoomId(roomId: Long, messageSearchServiceRequest: MessageSearchServiceRequest): List<Message>
}
