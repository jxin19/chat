package com.chat.homework.room.application.dto

data class ParticipantServiceResponses(
    val list: Set<ParticipantServiceResponse> = setOf(),
    val size: Int = 0
) {
    companion object {
        fun of(participantServiceResponses: Set<ParticipantServiceResponse>) =
            ParticipantServiceResponses(
                list = participantServiceResponses,
                size = participantServiceResponses.size
            )
    }
}
