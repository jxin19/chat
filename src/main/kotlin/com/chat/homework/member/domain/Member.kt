package com.chat.homework.member.domain

import com.chat.homework.member.domain.property.MemberName
import com.chat.homework.member.domain.property.Username
import jakarta.persistence.*

@Entity
@Table(
    name = "member",
    indexes = [
        Index(name = "idx_member_username", columnList = "username")
    ]
)
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false, updatable = false)
    val id: Long = 0,

    @Embedded
    private var name: MemberName? = null,

    @Embedded
    private var username: Username? = null
) {
    fun update(member: Member) {
        this.name = member.getName()
        this.username = member.getUsername()
    }

    private fun getName(): MemberName = name!!

    private fun getUsername(): Username = username!!

    val nameValue: String
        get() = name?.value ?: ""

    val usernameValue: String
        get() = username?.value ?: ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Member

        if (id != other.id) return false
        if (name != other.name) return false
        if (username != other.username) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + username.hashCode()
        return result
    }

    override fun toString(): String {
        return "Member(id=$id, name=$name, username=$username)"
    }
}
