package com.chat.homework.room.ui.dto

import com.chat.homework.room.application.dto.ParticipantServiceResponse

data class ParticipantResponse(
    val participantMemberId: Long,
    val participantMemberName: String
) {
    companion object {
        fun of(participantServiceResponse: ParticipantServiceResponse) = ParticipantResponse(
            participantServiceResponse.participantMemberId,
            participantServiceResponse.participantMemberName
        )
    }
}
