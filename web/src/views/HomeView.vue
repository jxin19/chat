<template>
  <div class="container">
    <LoginModal
      :is-open="showLogin"
      @login="handleLogin"
    />
    <div class="chat-layout">
      <RoomList
        v-if="!isChatting"
        @select="handleRoomSelect"
      />
      <div v-if="selectedRoom && loggedMember && isChatting" class="chat-view">
        <ChatRoom
          :room="selectedRoom"
          :member="loggedMember"
          @handleBack="handleBack"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import RoomList from "@/components/RoomList.vue"
import ChatRoom from "@/components/ChatRoom.vue"
import LoginModal from '@/components/LoginModal.vue'
import type { Room } from '@/types/room'
import type { Member } from "@/types/member.ts"

const showLogin = ref(true)
const loggedMember = ref<Member>()
const selectedRoom = ref<Room | null>(null)
const isChatting = ref(false)

const handleLogin = (member: Member) => {
  showLogin.value = false
  loggedMember.value = member
}

const handleRoomSelect = (room: Room) => {
  selectedRoom.value = room
  isChatting.value = true
}

const handleBack = () => {
  isChatting.value = false
}
</script>

<style scoped>
.container {
  padding: 20px;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.chat-layout {
  display: flex;
  gap: 20px;
  flex: 1;
  min-height: 0;
}

.chat-view {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.back-button span {
  font-size: 1.2rem;
}
</style>
