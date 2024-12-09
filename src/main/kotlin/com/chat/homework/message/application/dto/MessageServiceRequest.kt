package com.chat.homework.message.application.dto

data class MessageServiceRequest(
    val roomId: Long,
    val senderMemberId: Long,
    val senderMemberName: String,
    val content: String
) {
    fun toAvro() = MessageAvro.newBuilder()
        .setRoomId(roomId)
        .setSenderId(senderMemberId)
        .setSenderName(senderMemberName)
        .setContent(content)
        .setTimestamp(System.currentTimeMillis())
        .build()
}
