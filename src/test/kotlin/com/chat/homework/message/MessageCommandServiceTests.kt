package com.chat.homework.message

import com.chat.homework.common.config.KafkaConfig
import com.chat.homework.message.application.MessageCommandService
import com.chat.homework.message.application.dto.MessageServiceRequest
import com.chat.homework.message.repository.MessageRepository
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.Properties
import java.util.concurrent.TimeUnit

@SpringBootTest
class MessageCommandServiceIntegrationTests {

    companion object {
        private val network = Network.newNetwork()

        private val mongoContainer = MongoDBContainer(DockerImageName.parse("mongo:6.0"))
            .withNetwork(network)
            .withExposedPorts(27017)

        private val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
            .withNetwork(network)
            .withExposedPorts(9092)
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "false")
            .waitingFor(Wait.forLogMessage(".*started.*", 1))

        private val schemaRegistryContainer = GenericContainer(DockerImageName.parse("confluentinc/cp-schema-registry:7.4.0"))
            .withNetwork(network)
            .withExposedPorts(8081)
            .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
            .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://${kafkaContainer.networkAliases[0]}:9092")
            .waitingFor(Wait.forLogMessage(".*Schema Registry REST API endpoints.*", 1))

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.uri") {
                "mongodb://${mongoContainer.host}:${mongoContainer.firstMappedPort}/chat"
            }
            registry.add("spring.kafka.bootstrap-servers") {
                "${kafkaContainer.host}:${kafkaContainer.firstMappedPort}"
            }
            registry.add("spring.kafka.properties.schema.registry.url") {
                "http://${schemaRegistryContainer.host}:${schemaRegistryContainer.firstMappedPort}"
            }
        }

        init {
            mongoContainer.start()
            kafkaContainer.start()
            schemaRegistryContainer.start()
        }
    }

    @Autowired
    private lateinit var messageCommandService: MessageCommandService

    @Autowired
    private lateinit var messageRepository: MessageRepository

    @BeforeEach
    fun setUp() {
        messageRepository.deleteAll()
        createTopics()
    }

    private fun createTopics() {
        val properties = Properties().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.bootstrapServers)
        }

        AdminClient.create(properties).use { adminClient ->
            val topics = listOf(
                NewTopic(KafkaConfig.MESSAGE_TOPIC, 3, 1.toShort()),
                NewTopic(KafkaConfig.DLQ_TOPIC, 1, 1.toShort())
            )
            try {
                adminClient.createTopics(topics).all().get(30, TimeUnit.SECONDS)
            } catch (e: Exception) {
                println("Topics might already exist: ${e.message}")
            }
        }
    }

    @Test
    fun `대량_메시지_카프카_처리_테스트`() {
        // given
        val messageCount = 10000
        val roomCount = 10
        val startTime = System.currentTimeMillis()

        // when: 대량의 메시지를 카프카로 전송
        repeat(messageCount) { index ->
            val roomId = (index % roomCount + 1).toLong()
            val request = MessageServiceRequest(
                roomId = roomId,
                senderMemberId = index.toLong(),
                senderMemberName = "User$index",
                content = "Message content $index"
            )
            messageCommandService.storeMessage(request)
        }

        // then: 컨슈머가 메시지를 처리하여 MongoDB에 저장할 때까지 대기
        awaitMessagesProcessed(messageCount, Duration.ofMinutes(2))

        val processingTime = System.currentTimeMillis() - startTime
        val messagesPerSecond = messageCount * 1000.0 / processingTime

        // MongoDB에 저장된 메시지 검증
        val savedMessages = messageRepository.findAll()
        assertThat(savedMessages).hasSize(messageCount)

        // 룸별 메시지 분포 검증
        val messagesByRoom = savedMessages.groupBy { it.roomId }
        assertThat(messagesByRoom).hasSize(roomCount)
        messagesByRoom.forEach { (roomId, messages) ->
            assertThat(messages).hasSize(messageCount / roomCount)
            // 메시지 내용 검증
            messages.forEach { message ->
                assertThat(message.senderId).isGreaterThanOrEqualTo(0)
                assertThat(message.senderName).startsWith("User")
                assertThat(message.contentValue).startsWith("Message content")
                assertThat(message.roomId).isEqualTo(roomId)
            }
        }

        // 성능 지표 출력
        println("=== 성능 테스트 결과 ===")
        println("총 메시지 수: $messageCount")
        println("총 처리 시간: ${processingTime}ms")
        println("초당 처리 메시지: %.2f".format(messagesPerSecond))
        println("평균 메시지 처리 시간: %.2fms".format(processingTime.toDouble() / messageCount))
    }

    private fun awaitMessagesProcessed(expectedCount: Int, timeout: Duration) {
        val startTime = System.currentTimeMillis()
        val timeoutMs = timeout.toMillis()

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            val count = messageRepository.count()
            if (count >= expectedCount) {
                // 모든 메시지가 처리되었으므로 약간의 시간을 더 기다려 후처리가 완료되도록 함
                Thread.sleep(1000)
                return
            }
            println("Processed ${count}/${expectedCount} messages...")
            Thread.sleep(1000)
        }

        val actualCount = messageRepository.count()
        throw AssertionError("""
            |시간 초과: $timeout
            |예상 메시지 수: $expectedCount
            |실제 처리된 메시지 수: $actualCount
            |처리되지 않은 메시지 수: ${expectedCount - actualCount}
        """.trimMargin())
    }

    class GenericContainer(imageName: DockerImageName) :
        org.testcontainers.containers.GenericContainer<GenericContainer>(imageName)
}
