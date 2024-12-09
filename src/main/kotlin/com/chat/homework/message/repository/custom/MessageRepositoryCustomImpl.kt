package com.chat.homework.message.repository.custom

import com.chat.homework.message.application.dto.MessageSearchServiceRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import com.chat.homework.message.domain.Message
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository
class MessageRepositoryCustomImpl(
    private val mongoTemplate: MongoTemplate
) : MessageRepositoryCustom {

    override fun findLastMessagesByRoomId(
        roomId: Long,
        messageSearchServiceRequest: MessageSearchServiceRequest
    ): List<Message> {
        val criteria = Criteria.where("room_id").`is`(roomId)
        val query = Query(criteria)
            .with(Sort.by(Sort.Direction.DESC, "created_at"))
            .skip((messageSearchServiceRequest.page - 1) * messageSearchServiceRequest.size.toLong())
            .limit(messageSearchServiceRequest.size)

        return mongoTemplate.find(query, Message::class.java)
    }

}
