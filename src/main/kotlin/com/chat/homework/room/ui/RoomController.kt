package com.chat.homework.room.ui

import com.chat.homework.room.application.RoomCommandService
import com.chat.homework.room.application.RoomQueryService
import com.chat.homework.room.ui.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.web.bind.annotation.*

@Tag(name = "채팅방 API", description = "채팅방 정보를 관리하는 API")
@RestController
@RequestMapping("/room")
class RoomController(
    private val roomQueryService: RoomQueryService,
    private val roomCommandService: RoomCommandService,
    private val messageSendingOperations: SimpMessageSendingOperations,
) {

    @Operation(summary = "채팅방 목록 조회", description = "채팅방의 목록 정보를 조회합니다.")
    @GetMapping("/list")
    fun list(): RoomResponses {
        val response = roomQueryService.getList()
        return RoomResponses.of(response)
    }

    @Operation(summary = "채팅방 상세 조회", description = "ID를 사용하여 채팅방의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    fun detail(@PathVariable id: Long): RoomResponse {
        val response = roomQueryService.getRoomById(id)
        return RoomResponse.of(response)
    }

    @Operation(summary = "채팅방 생성", description = "새로운 채팅방를 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: RoomRequest): RoomResponse {
        val response = roomCommandService.create(request.toServiceDto())
        return RoomResponse.of(response)
    }

    @Operation(summary = "채팅방 수정", description = "ID를 사용하여 기존 채팅방 정보를 수정합니다.")
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: RoomRequest
    ): RoomResponse {
        val response = roomCommandService.update(id, request.toServiceDto())
        return RoomResponse.of(response)
    }

    @Operation(summary = "채팅방 삭제", description = "ID를 사용하여 기존 채팅방 정보를 삭제합니다.")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = roomCommandService.delete(id)

    @MessageMapping("/room/{roomId}/enter")
    fun handleEnter(@DestinationVariable roomId: Long, @Payload participantRequest: ParticipantRequest) {
        val participants = roomCommandService.handleEnter(roomId, participantRequest.toServiceDto())
        messageSendingOperations.convertAndSend(
            "/sub/room/$roomId/participants",
            ParticipantResponses.of(participants)
        )
    }

    @MessageMapping("/room/{roomId}/leave")
    fun handleLeave(@DestinationVariable roomId: Long, @Payload participantRequest: ParticipantRequest) {
        val participants = roomCommandService.handleLeave(roomId, participantRequest.toServiceDto())
        messageSendingOperations.convertAndSend(
            "/sub/room/$roomId/participants",
            ParticipantResponses.of(participants)
        )
    }

}
