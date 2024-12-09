package com.chat.homework.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.handler.TextWebSocketHandler

@Configuration
@EnableWebSocketMessageBroker
class WebSocketBrokerConfig : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/sub")
        registry.setApplicationDestinationPrefixes("/pub")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws-chat")
            .setAllowedOriginPatterns("*")
            .withSockJS()
    }

    @Bean
    fun webSocketHandler(): WebSocketHandler {
        return object : TextWebSocketHandler() {
            override fun afterConnectionEstablished(session: WebSocketSession) {
                println("WebSocket 연결 성공: ${session.id}")
            }

            override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
                println("WebSocket 연결 종료: ${session.id}, 상태: $status")
            }
        }
    }

}
