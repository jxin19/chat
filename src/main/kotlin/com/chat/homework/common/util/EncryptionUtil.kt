package com.chat.homework.common.util

import com.chat.homework.common.config.EncryptionKeyProvider
import org.springframework.stereotype.Component
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom
import java.util.Base64

@Component
class EncryptionUtil {

    companion object {
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val TAG_LENGTH = 128
        private const val NONCE_SIZE = 12

        private val secureRandom = SecureRandom()
        private var testKey: ByteArray? = null

        fun encrypt(data: String): String {
            val encryptionKeyProvider = EncryptionKeyProvider()
            val cipher = Cipher.getInstance(TRANSFORMATION)

            val key = SecretKeySpec(testKey ?: encryptionKeyProvider.getKey(), ALGORITHM)
            val nonce = ByteArray(NONCE_SIZE)
            secureRandom.nextBytes(nonce)

            val gcmParameterSpec = GCMParameterSpec(TAG_LENGTH, nonce)
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec)
            val encryptedBytes = cipher.doFinal(data.toByteArray())

            val combined = nonce + encryptedBytes
            return Base64.getEncoder().encodeToString(combined)
        }

        fun decrypt(encryptedData: String): String {
            try {
                val encryptionKeyProvider = EncryptionKeyProvider()
                val cipher = Cipher.getInstance(TRANSFORMATION)

                val combined = Base64.getDecoder().decode(encryptedData)
                val nonce = combined.copyOfRange(0, NONCE_SIZE)
                val encryptedBytes = combined.copyOfRange(NONCE_SIZE, combined.size)

                val key = SecretKeySpec(testKey ?: encryptionKeyProvider.getKey(), ALGORITHM)
                val gcmParameterSpec = GCMParameterSpec(TAG_LENGTH, nonce)
                cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec)

                val decryptedBytes = cipher.doFinal(encryptedBytes)
                return String(decryptedBytes)
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid base64 input: ${e.message}")
            }
        }

        fun setTestKey(key: String) {
            testKey = key.toByteArray()
        }

        fun clearTestKey() {
            testKey = null
        }
    }
}
