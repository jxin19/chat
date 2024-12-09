// components/RoomList.vue
<template>
  <div class="room-list">
    <div class="room-list-header">
      <h2>채팅방 목록</h2>
      <button @click="createRoom" class="create-btn">새 채팅방</button>
    </div>

    <div v-if="loading" class="loading">
      로딩 중...
    </div>

    <div v-else-if="roomStore.list.length === 0" class="empty-state">
      참여 중인 채팅방이 없습니다.
    </div>

    <div v-else class="room-items">
      <div
        v-for="room in roomStore.list"
        :key="room.id"
        class="room-item"
        :class="{
          active: selectedRoomId === room.id,
          disabled: room.maxParticipants <= room.participants
        }"
        @click="selectRoom(room)"
      >
        <div class="room-info">
          <h3 class="room-name">{{ room.name }}</h3>
          <span class="room-type">
            {{ room.roomType === 'ONE_TO_ONE' ? '1:1 채팅' : '그룹 채팅' }}
          </span>
        </div>
        <div class="room-meta">
          <span class="participant-count">
            참여자 {{ room.participants || 0 }}/{{ room.maxParticipants }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {useRoomStore} from "@/stores/roomStore.ts";
import type { Room } from '@/types/room'

const roomStore = useRoomStore()
const loading = ref(true)
const selectedRoomId = ref<number | null>(null)

const emit = defineEmits<{
  (e: 'select', room: Room): void
}>()

onMounted(async () => {
  try {
    await roomStore.fetchList()
  } finally {
    loading.value = false
  }
})

const selectRoom = (room: Room) => {
  if (room.maxParticipants > room.participants) {
    selectedRoomId.value = room.id
    emit('select', room)
  }
}

const createRoom = () => {
  // TODO: 채팅방 생성 모달 열기
}
</script>

<style scoped>
.room-list {
  border-right: 1px solid #eee;
  width: 100%;
  height: 100%;
  background: white;
}

.room-list-header {
  padding: 1rem;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.create-btn {
  padding: 0.5rem 1rem;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.create-btn:hover {
  background: #0056b3;
}

.loading,
.empty-state {
  padding: 2rem;
  text-align: center;
  color: #666;
}

.room-items {
  overflow-y: auto;
  height: calc(100% - 60px);
}

.room-item {
  padding: 1rem;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.2s;
}

.room-item:hover {
  background: #f8f9fa;
}

.room-item.active {
  background: #e9ecef;
}

.room-info {
  margin-bottom: 0.5rem;
}

.room-name {
  margin: 0;
  font-size: 1rem;
  font-weight: 500;
}

.room-type {
  font-size: 0.8rem;
  color: #666;
}

.room-meta {
  font-size: 0.8rem;
  color: #666;
}

.participant-count {
  background: #f1f3f5;
  padding: 0.2rem 0.5rem;
  border-radius: 12px;
}

.room-item.disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background: #f5f5f5;
}

.room-item.disabled:hover {
  background: #f5f5f5;
}

.participant-count.is-full {
  background: #fee5e5;
  color: #dc3545;
}
</style>
