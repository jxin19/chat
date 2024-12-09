package com.chat.homework.room.application.extension

import com.chat.homework.room.application.dto.ParticipantServiceRequest
import com.chat.homework.room.application.dto.ParticipantServiceResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

private val objectMapper = jacksonObjectMapper()

fun ParticipantServiceRequest.toRedisData(): String {
    val response = ParticipantServiceResponse(
        participantMemberId = participantMemberId,
        participantMemberName = participantMemberName
    )
    return objectMapper.writeValueAsString(response)
}

fun String.toDto(): ParticipantServiceResponse =
    objectMapper.readValue(this, ParticipantServiceResponse::class.java)

