package com.chat.homework.common.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedissonConfig(
    @Value("\${spring.redis.host}")
    private val host: String,

    @Value("\${spring.redis.port}")
    private val port: Int,

) {
    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()

        val address = "redis://$host:$port"
        config.useSingleServer()
            .setAddress(address)

        return Redisson.create(config)
    }
}
