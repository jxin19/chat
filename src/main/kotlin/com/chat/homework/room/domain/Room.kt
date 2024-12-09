package com.chat.homework.room.domain

import com.chat.homework.room.domain.property.MaxParticipants
import com.chat.homework.room.domain.property.RoomName
import com.chat.homework.room.domain.property.RoomStatus
import com.chat.homework.room.domain.property.RoomType
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@Entity
@Table(
    name = "room",
)
class Room(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id", nullable = false, updatable = false)
    val id: Long = 0,

    @Embedded
    private var name: RoomName? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private var roomType: RoomType? = null,

    @Embedded
    private var maxParticipants: MaxParticipants? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private var status: RoomStatus? = null,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        if (roomType == RoomType.ONE_TO_ONE) {
            maxParticipants = MaxParticipants(2)
        }
    }

    fun update(room: Room) {
        this.name = room.getName()
        this.roomType = room.getRoomType()
        this.maxParticipants = room.getMaxParticipants()
        this.status = room.getStatus()
    }

    fun updateDeletionStatus() {
        this.status = RoomStatus.DELETED
    }

    private fun getName(): RoomName = name!!

    private fun getRoomType(): RoomType = roomType!!

    private fun getMaxParticipants(): MaxParticipants = maxParticipants!!

    private fun getStatus(): RoomStatus = status!!

    val roomNameValue: String
        get() = name?.value ?: ""

    val roomTypeValue: String
        get() = roomType?.name ?: ""

    val maxParticipantsValue: Int
        get() = maxParticipants?.value ?: 0

    val statusValue: String
        get() = status?.name ?: ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Room

        if (id != other.id) return false
        if (name != other.name) return false
        if (roomType != other.roomType) return false
        if (maxParticipants != other.maxParticipants) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + roomType.hashCode()
        result = 31 * result + maxParticipants.hashCode()
        return result
    }

    override fun toString(): String {
        return "Room(id=$id, name=$name, roomType=$roomType, maxParticipants=$maxParticipants, status=$status, createdAt=$createdAt)"
    }

}
