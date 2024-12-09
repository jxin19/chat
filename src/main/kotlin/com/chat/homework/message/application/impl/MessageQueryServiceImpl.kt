package com.chat.homework.message.application.impl

import com.chat.homework.message.application.MessageQueryService
import com.chat.homework.message.application.dto.MessageSearchServiceRequest
import com.chat.homework.message.application.dto.MessageServiceResponses
import com.chat.homework.message.domain.Message
import com.chat.homework.message.repository.MessageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MessageQueryServiceImpl(
    private val messageRepository: MessageRepository
) : MessageQueryService {

    override fun getMessages(roomId: Long, messageSearchServiceRequest: MessageSearchServiceRequest): MessageServiceResponses {
        val list: List<Message> = messageRepository.findLastMessagesByRoomId(roomId, messageSearchServiceRequest)
        return MessageServiceResponses.of(list)
    }

}
