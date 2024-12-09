package com.chat.homework.message.application.impl

import com.chat.homework.common.config.KafkaConfig
import com.chat.homework.message.application.MessageCommandService
import com.chat.homework.message.application.dto.MessageAvro
import com.chat.homework.message.application.dto.MessageServiceRequest
import com.chat.homework.message.domain.Message
import com.chat.homework.message.domain.property.MessageContent
import com.chat.homework.message.repository.MessageRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class MessageCommandServiceImpl(
    private val messageRepository: MessageRepository,
    private val kafkaTemplate: KafkaTemplate<String, MessageAvro>,
) : MessageCommandService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun storeMessage(messageServiceRequest: MessageServiceRequest) {
        kafkaTemplate.send(
            KafkaConfig.MESSAGE_TOPIC,
            messageServiceRequest.roomId.toString(),
            messageServiceRequest.toAvro()
        )
    }

    @KafkaListener(
        topics = [KafkaConfig.MESSAGE_TOPIC],
        groupId = KafkaConfig.MESSAGE_CONSUMER_GROUP,
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(
        value = [Exception::class],
        maxAttempts = KafkaConfig.MAX_RETRY_ATTEMPTS,
        backoff = Backoff(delay = 1000, multiplier = 2.0, maxDelay = 6000)
    )
    fun consumeMessage(newMessages: List<ConsumerRecord<String, MessageAvro>>, acknowledgment: Acknowledgment) {
        val messages = newMessages.map {
            val messageAvro = it.value()
            Message(
                roomId = messageAvro.roomId,
                senderId = messageAvro.senderId,
                senderName = messageAvro.senderName,
                content = MessageContent(messageAvro.content),
                createdAt = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(messageAvro.timestamp),
                    ZoneId.of("Asia/Seoul")
                ),
            )
        }

        messageRepository.insert(messages)

        acknowledgment.acknowledge()
    }

    @KafkaListener(
        topics = [KafkaConfig.DLQ_TOPIC],
        groupId = "${KafkaConfig.MESSAGE_CONSUMER_GROUP}-dlq",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun consumeDlqMessage(newMessages: List<ConsumerRecord<String, MessageAvro>>, acknowledgment: Acknowledgment) {
        try {
            newMessages.forEach { record ->
                val messageAvro = record.value()
                logger.error(
                    "Message moved to DLQ - Room: {}, Sender: {}, Content: {}",
                    messageAvro.roomId,
                    messageAvro.senderId,
                    messageAvro.content
                )
                // 여기서 알림 발송이나 모니터링 시스템에 전송하는 로직 추가
            }
            acknowledgment.acknowledge()
        } catch (e: Exception) {
            logger.error("Error processing DLQ messages", e)
            throw e
        }
    }

}
