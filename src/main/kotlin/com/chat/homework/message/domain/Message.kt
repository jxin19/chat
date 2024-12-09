package com.chat.homework.message.domain

import com.chat.homework.message.domain.property.MessageContent
import jakarta.persistence.*

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

@Document(collection = "messages")
data class Message(
    @Id
    val id: String? = null,

    @Field("room_id")
    @Indexed
    val roomId: Long,

    @Field("sender_id")
    @Indexed
    val senderId: Long,

    @Field("sender_name")
    val senderName: String,

    @Field("content")
    val content: MessageContent,

    @Field("created_at")
    @Indexed
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    val contentValue
        get() = content.value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (roomId != other.roomId) return false
        if (senderId != other.senderId) return false
        if (id != other.id) return false
        if (content != other.content) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = roomId.hashCode()
        result = 31 * result + senderId.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + content.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }

    override fun toString(): String {
        return "Message(id=$id, roomId=$roomId, senderId=$senderId, senderName='$senderName', content=$content, createdAt=$createdAt)"
    }

}
