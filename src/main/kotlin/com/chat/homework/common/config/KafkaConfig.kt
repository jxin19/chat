package com.chat.homework.common.config

import com.chat.homework.message.application.dto.MessageAvro
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaConfig {

    companion object {
        const val MESSAGE_CONSUMER_GROUP = "chat-message-processor"
        const val MESSAGE_TOPIC = "chat.messages"
        const val DLQ_TOPIC = "chat.messages.dlq"
        const val MAX_RETRY_ATTEMPTS = 3
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, MessageAvro>): KafkaTemplate<String, MessageAvro> {
        return KafkaTemplate(producerFactory)
    }

}
