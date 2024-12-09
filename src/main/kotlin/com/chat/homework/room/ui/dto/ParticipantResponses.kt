package com.chat.homework.room.ui.dto

import com.chat.homework.room.application.dto.ParticipantServiceResponses

data class ParticipantResponses(
    val list: List<ParticipantResponse>
) {
    companion object {
        fun of(participantServiceResponses: ParticipantServiceResponses) =
            ParticipantResponses(participantServiceResponses.list.map { ParticipantResponse.of(it) })
    }
}
