package com.chat.homework.room

import com.chat.homework.room.domain.Room
import com.chat.homework.room.domain.property.MaxParticipants
import com.chat.homework.room.domain.property.RoomName
import com.chat.homework.room.domain.property.RoomStatus
import com.chat.homework.room.domain.property.RoomType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class RoomTests {

    @Test
    fun `채팅방_생성_성공`() {
        // given
        val roomName = RoomName("개발자 모임방")
        val roomType = RoomType.GROUP
        val maxParticipants = MaxParticipants(10)
        val status = RoomStatus.ACTIVE

        // when
        val room = Room(
            name = roomName,
            roomType = roomType,
            maxParticipants = maxParticipants,
            status = status
        )

        // then
        assertThat(room.roomNameValue).isEqualTo("개발자 모임방")
        assertThat(room.roomTypeValue).isEqualTo("GROUP")
        assertThat(room.maxParticipantsValue).isEqualTo(10)
        assertThat(room.statusValue).isEqualTo("ACTIVE")
    }

    @Test
    fun `1대1_채팅방_생성시_최대_참여자수_2명으로_설정`() {
        // given
        val roomName = RoomName("1:1 채팅방")
        val roomType = RoomType.ONE_TO_ONE
        val status = RoomStatus.ACTIVE

        // when
        val room = Room(
            name = roomName,
            roomType = roomType,
            maxParticipants = MaxParticipants(10), // 더 큰 값을 설정해도
            status = status
        )

        // then
        assertThat(room.maxParticipantsValue).isEqualTo(2) // 2명으로 강제 설정됨
    }

    @Test
    fun `채팅방_정보_수정_성공`() {
        // given
        val room = Room(
            name = RoomName("이전 채팅방"),
            roomType = RoomType.GROUP,
            maxParticipants = MaxParticipants(10),
            status = RoomStatus.ACTIVE
        )
        val updatedRoom = Room(
            name = RoomName("수정된 채팅방"),
            roomType = RoomType.GROUP,
            maxParticipants = MaxParticipants(20),
            status = RoomStatus.ACTIVE
        )

        // when
        room.update(updatedRoom)

        // then
        assertThat(room.roomNameValue).isEqualTo("수정된 채팅방")
        assertThat(room.maxParticipantsValue).isEqualTo(20)
    }

    @Test
    fun `채팅방_삭제_상태_변경_성공`() {
        // given
        val room = Room(
            name = RoomName("채팅방"),
            roomType = RoomType.GROUP,
            maxParticipants = MaxParticipants(10),
            status = RoomStatus.ACTIVE
        )

        // when
        room.updateDeletionStatus()

        // then
        assertThat(room.statusValue).isEqualTo("DELETED")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " "])
    fun `방제목이_빈값이면_예외발생`(emptyName: String) {
        assertThatThrownBy { RoomName(emptyName) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("방제목을 입력해주세요.")
    }

    @Test
    fun `방제목이_100자_초과하면_예외발생`() {
        // given
        val longName = "a".repeat(101)

        // when & then
        assertThatThrownBy { RoomName(longName) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("방제목은 1~100 이내로 입력해 주세요.")
    }

    @Test
    fun `최대_참여자수가_2명_미만이면_예외발생`() {
        assertThatThrownBy { MaxParticipants(1) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("최대 인원수는 2 ~ 100 명 이내로 설정해주세요.")
    }

    @Test
    fun `최대_참여자수가_100명_초과하면_예외발생`() {
        assertThatThrownBy { MaxParticipants(101) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("최대 인원수는 2 ~ 100 명 이내로 설정해주세요.")
    }

    @Test
    fun `잘못된_채팅방_유형_입력시_예외발생`() {
        assertThatThrownBy { RoomType.fromValue("INVALID_TYPE") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("잘못된 채팅방 유형입니다. - INVALID_TYPE")
    }

    @Test
    fun `잘못된_채팅방_상태_입력시_예외발생`() {
        assertThatThrownBy { RoomStatus.fromValue("INVALID_STATUS") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("잘못된 채팅방 상태입니다. - INVALID_STATUS")
    }

    @Test
    fun `동일한_채팅방_비교시_true_반환`() {
        // given
        val room1 = Room(
            id = 1L,
            name = RoomName("채팅방"),
            roomType = RoomType.GROUP,
            maxParticipants = MaxParticipants(10),
            status = RoomStatus.ACTIVE
        )
        val room2 = Room(
            id = 1L,
            name = RoomName("채팅방"),
            roomType = RoomType.GROUP,
            maxParticipants = MaxParticipants(10),
            status = RoomStatus.ACTIVE
        )

        // when & then
        assertThat(room1).isEqualTo(room2)
    }

    @Test
    fun `다른_채팅방_비교시_false_반환`() {
        // given
        val room1 = Room(
            id = 1L,
            name = RoomName("채팅방1"),
            roomType = RoomType.GROUP,
            maxParticipants = MaxParticipants(10),
            status = RoomStatus.ACTIVE
        )
        val room2 = Room(
            id = 2L,
            name = RoomName("채팅방2"),
            roomType = RoomType.ONE_TO_ONE,
            maxParticipants = MaxParticipants(2),
            status = RoomStatus.ACTIVE
        )

        // when & then
        assertThat(room1).isNotEqualTo(room2)
    }

    @Test
    fun `최대_참여자수_초과_확인_성공`() {
        // given
        val maxParticipants = MaxParticipants(5)

        // when & then
        assertThat(maxParticipants.isAlreadyExceed(3)).isTrue() // 현재 참여자 수가 최대 참여자 수보다 적음
        assertThat(maxParticipants.isAlreadyExceed(6)).isFalse() // 현재 참여자 수가 최대 참여자 수보다 많음
    }
}
