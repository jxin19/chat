package com.chat.homework.room.ui.dto

import com.chat.homework.room.application.dto.RoomServiceRequest
import com.chat.homework.room.domain.property.RoomStatus
import com.chat.homework.room.domain.property.RoomType
import jakarta.validation.constraints.NotBlank

data class RoomRequest(
    @field:NotBlank(message = "채팅방 이름을 입력해주세요.")
    val name: String,

    @field:NotBlank(message = "채팅방 유형을 선택해주세요.")
    val roomType: String,

    val maxParticipants: Int = 2,

    @field:NotBlank(message = "채팅방 상태를 선택해주세요.")
    val status: String,
) {
    fun toServiceDto() = RoomServiceRequest(
        name = name,
        roomType = RoomType.fromValue(roomType),
        maxParticipants = maxParticipants,
        status = RoomStatus.valueOf(status),
    )
}
