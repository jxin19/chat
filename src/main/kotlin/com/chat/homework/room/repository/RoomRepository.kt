package com.chat.homework.room.repository

import com.chat.homework.room.domain.Room
import com.chat.homework.room.domain.property.RoomStatus
import org.springframework.data.repository.CrudRepository

interface RoomRepository : CrudRepository<Room, Long> {
    fun findRoomsByStatusIs(roomStatus: RoomStatus): List<Room>
}
