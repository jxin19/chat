package com.chat.homework.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Base64

@Component
class EncryptionKeyProvider {

    @Value("\${encryption.key}")
    private var base64Key: String = "MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="

    fun getKey(): ByteArray {
        return Base64.getDecoder().decode(base64Key)
    }

}
