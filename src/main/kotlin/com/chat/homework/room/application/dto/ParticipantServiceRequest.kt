package com.chat.homework.room.application.dto

data class ParticipantServiceRequest(
    val participantMemberId: Long,
    val participantMemberName: String
)
