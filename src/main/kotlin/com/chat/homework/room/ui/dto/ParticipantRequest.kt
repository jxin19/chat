package com.chat.homework.room.ui.dto

import com.chat.homework.room.application.dto.ParticipantServiceRequest

data class ParticipantRequest(
    val participantMemberId: Long,
    val participantMemberName: String
) {
    fun toServiceDto() = ParticipantServiceRequest(participantMemberId, participantMemberName)
}
