package com.chat.homework.member.domain.property

import com.chat.homework.common.domain.StringValidator
import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
@Access(AccessType.FIELD)
class MemberName : StringValidator {

    @Column(name = "name", nullable = false, length = MAX_LENGTH)
    private var _value: String

    constructor(name: String) {
        validateLength(name, "이름을 입력해주세요.")
        validateRule(name, REGEX, "이름은 $MIN_LENGTH~$MAX_LENGTH 이내로 입력해 주세요.")
        this._value = name.trim()
    }

    val value: String
        get() = _value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MemberName

        return _value == other._value
    }

    override fun hashCode(): Int {
        return _value.hashCode()
    }

    override fun toString(): String {
        return "MemberName(_value='$_value')"
    }

    companion object {
        private const val MIN_LENGTH: Int = 1
        private const val MAX_LENGTH: Int = 100
        private const val REGEX: String = "^.{$MIN_LENGTH,$MAX_LENGTH}$"
    }

}
