package com.chat.homework.config

import com.chat.homework.common.config.EncryptionKeyProvider
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.util.ReflectionTestUtils

@TestConfiguration
class TestConfig {
    @Bean
    fun encryptionKeyProvider(): EncryptionKeyProvider {
        return EncryptionKeyProvider().apply {
            ReflectionTestUtils.setField(this, "base64Key", "your_test_key_here")
        }
    }
}
