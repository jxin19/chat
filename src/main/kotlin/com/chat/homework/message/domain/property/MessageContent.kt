package com.chat.homework.message.domain.property

import com.chat.homework.common.domain.StringValidator
import org.springframework.data.mongodb.core.mapping.Document

@Document
class MessageContent : StringValidator {

    private var _value: String

    constructor(content: String) {
        validateLength(content, "메시지 내용을 입력해주세요.")
        validateRule(content, REGEX, "메시지는 $MIN_LENGTH~$MAX_LENGTH 이내로 입력해 주세요.")
        this._value = content.trim()
    }

    val value: String
        get() = _value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessageContent

        return _value == other._value
    }

    override fun hashCode(): Int {
        return _value.hashCode()
    }

    override fun toString(): String {
        return "MessageContent(_value='$_value')"
    }

    companion object {
        private const val MIN_LENGTH: Int = 1
        private const val MAX_LENGTH: Int = 1000
        private const val REGEX: String = "^.{$MIN_LENGTH,$MAX_LENGTH}$"
    }

}
