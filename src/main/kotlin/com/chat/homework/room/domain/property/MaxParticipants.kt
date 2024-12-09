package com.chat.homework.room.domain.property

import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
@Access(AccessType.FIELD)
class MaxParticipants {

    @Column(name = "max_participants", nullable = false, length = MAX_VALUE)
    private var _value: Int

    constructor(maxParticipants: Int) {
        require(maxParticipants in MIN_VALUE..MAX_VALUE) { "최대 인원수는 $MIN_VALUE ~ $MAX_VALUE 명 이내로 설정해주세요." }
        this._value = maxParticipants
    }

    val value: Int
        get() = _value

    fun isAlreadyExceed(currentParticipants: Int) = _value >= currentParticipants

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MaxParticipants

        return _value == other._value
    }

    override fun hashCode(): Int {
        return _value
    }

    override fun toString(): String {
        return "MaxParticipants(_value=$_value)"
    }

    companion object {
        private const val MIN_VALUE: Int = 2
        private const val MAX_VALUE: Int = 100
    }

}
