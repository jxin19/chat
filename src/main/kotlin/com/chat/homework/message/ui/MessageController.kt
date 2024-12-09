package com.chat.homework.message.ui

import com.chat.homework.message.application.MessageCommandService
import com.chat.homework.message.application.MessageQueryService
import com.chat.homework.message.ui.dto.MessageRequest
import com.chat.homework.message.ui.dto.MessageResponses
import com.chat.homework.message.ui.dto.MessageSearchRequest
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.web.bind.annotation.*

@Tag(name = "메시지 통신", description = "메시지 정보를 통신하는 API")
@RestController
class MessageController(
    private val messageCommandService: MessageCommandService,
    private val messageQueryService: MessageQueryService,
    private val messageSendingOperations: SimpMessageSendingOperations,
) {

    @GetMapping("/message/{roomId}")
    fun getMessages(
        @PathVariable roomId: Long,
        @ModelAttribute messageSearchRequest: MessageSearchRequest,
    ): MessageResponses {
        val response = messageQueryService.getMessages(roomId, messageSearchRequest.toServiceDto())
        return MessageResponses.of(response)
    }

    @MessageMapping("/room/{roomId}/send")
    fun sendMessageToRoom(@Payload request: MessageRequest, @DestinationVariable roomId: Long) {
        CoroutineScope(Dispatchers.Default).launch {
            coroutineScope {
                launch { messageCommandService.storeMessage(request.toServiceDto()) }
                launch {
                    val subscribeAddress = "/sub/room/$roomId/info"
                    messageSendingOperations.convertAndSend(subscribeAddress, request)
                }
            }
        }
    }

}
