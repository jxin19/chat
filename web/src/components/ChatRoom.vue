<!-- ChatRoom.vue -->
<template>
  <button class="back-button" @click="handleBack">
    <span>&larr;</span> 뒤로가기
  </button>
  <div class="chat-container">
    <div class="room-header">
      <h3>{{ currentRoom?.name }}</h3>
      <span class="room-info">
        {{ currentRoom?.roomType === 'ONE_TO_ONE' ? '1:1 채팅' : '그룹 채팅' }} |
        <button class="participants-button" @click="showParticipantsModal = true">
          참여자 {{ currentRoom?.participants || 0 }}/{{ currentRoom?.maxParticipants }}
        </button>
      </span>
    </div>
    <div class="message-area">
      <div class="message-list" ref="messageListRef">
        <button
          class="load-previous-button"
          @click="loadPreviousMessages"
          :disabled="isLoading"
        >
          {{ isLoading ? '로딩 중...' : '이전 채팅 읽기' }}
        </button>
        <ul>
          <li v-for="(msg, index) in messages" :key="index">{{ msg }}</li>
        </ul>
      </div>
    </div>
    <div class="input-area">
      <div class="input-wrapper">
        <input
          v-model="inputMessage"
          @keyup.enter="sendMessage"
          placeholder="메시지를 입력하세요"
          class="message-input"
          maxlength="1000"
        />
        <span class="character-count" :class="{ 'near-limit': inputMessage.length >= 900 }">
         {{ inputMessage.length }}/1000
       </span>
      </div>
      <button
        @click="sendMessage"
        class="send-button"
        :disabled="!inputMessage.trim() || inputMessage.length > 1000"
      >
        전송
      </button>
    </div>
  </div>
  <ParticipantListModal
    v-if="showParticipantsModal"
    @handleCloseModal="showParticipantsModal = false"
  />
</template>

<script setup lang="ts">
import {ref, watch, computed, nextTick, onMounted, onBeforeUnmount} from 'vue'
import type { Room } from '@/types/room'
import {useChatStore} from "@/stores/chatStore.ts";
import type {Member} from "@/types/member.ts";
import ParticipantListModal from "@/components/ParticipantListModal.vue";
const chatStore = useChatStore()

const props = defineProps<{
  room: Room,
  member: Member
}>()

const currentRoom = computed(() => chatStore.currentRoom)
const messages = computed(() => chatStore.messages)
const showParticipantsModal = ref(false)
const messageListRef = ref<HTMLElement | null>(null)
const inputMessage = ref('')
const isLoading = ref(false)
const currentPage = ref(1)

const sendMessage = () => {
  if (inputMessage.value.trim() && inputMessage.value.length <= 1000) {
    chatStore.sendMessage(inputMessage.value)
    inputMessage.value = ''
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

const loadPreviousMessages = async () => {
  try {
    isLoading.value = true
    await chatStore.fetchLastMessagesInRoom(props.room.id, currentPage.value)
    currentPage.value++
  } catch (error) {
    console.error('이전 메시지 로딩 실패:', error)
  } finally {
    isLoading.value = false
  }
}

const handleBack = () => {
  chatStore.leaveRoom()
  emit('handleBack')
}

const handleClose = () => {
  chatStore.leaveRoom()
};

onMounted(() => {
  window.addEventListener('beforeunload', handleClose);
  window.addEventListener('unload', handleClose);
});

onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', handleClose);
  window.removeEventListener('unload', handleClose);
});

watch(() => props.room, (newRoom) => {
  chatStore.setCurrentRoom(newRoom, props.member)
  scrollToBottom()
}, { immediate: true })

watch(currentRoom, (newRoom) => {
  if (newRoom && props.room.id === newRoom.id) {
    emit('update:room', newRoom)
  }
}, { deep: true })

watch(() => messages.value, () => {
  scrollToBottom()
}, { deep: true })

const emit = defineEmits<{
  (e: 'update:room', room: Room): void
  (e: 'handleBack'): void
}>()
</script>

<style scoped>
.back-button {
  padding: 8px 16px;
  margin-bottom: 10px;
  background: none;
  border: none;
  font-size: 1rem;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;

  &:hover {
    color: #333;
  }
}

.chat-container {
    display: flex;
    flex-direction: column;
    height: 100%;
    width: 100%;
    min-height: 600px;
    border: 1px solid #ddd;
    border-radius: 8px;

  .room-header {
    padding: 15px 20px;
    border-bottom: 1px solid #ddd;
    background-color: white;
  }

  .room-header h3 {
    margin: 0;
    margin-bottom: 5px;
  }

  .room-info {
    font-size: 0.8rem;
    color: #666;
  }

  .participants-button {
    background: none;
    border: none;
    color: #007bff;
    cursor: pointer;
    padding: 0;
    font-size: inherit;
    text-decoration: underline;
  }

  .message-area {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    background-color: #f9f9f9;
  }

  .message-list {
    display: flex;
    flex-direction: column;
    height: 100%;
    overflow-y: auto;

    .load-previous-button {
      width: 100%;
      padding: 10px;
      margin-bottom: 15px;
      background-color: #f8f9fa;
      border: 1px solid #dee2e6;
      border-radius: 4px;
      color: #495057;
      cursor: pointer;
      font-size: 0.9rem;
      transition: all 0.2s ease;

      &:hover {
        background-color: #e9ecef;
      }

      &:disabled {
        background-color: #e9ecef;
        cursor: not-allowed;
        opacity: 0.7;
      }
    }

    ul {
      list-style: none;
      padding: 0;
      margin: 0;
    }

    li {
      background-color: white;
      padding: 10px;
      margin-bottom: 10px;
      border-radius: 8px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
    }
  }

  .input-wrapper {
    position: relative;
    flex: 1;
    display: flex;
    align-items: center;
  }

  .input-area {
    display: flex;
    padding: 20px;
    gap: 10px;
    background-color: white;
    border-top: 1px solid #ddd;
  }

  .message-input {
    width: 100%;
    padding: 10px;
    padding-right: 70px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
  }

  .character-count {
    position: absolute;
    right: 10px;
    color: #666;
    font-size: 12px;
  }

  .character-count.near-limit {
    color: #dc3545;
  }

  .send-button {
    padding: 10px 20px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;

    &:disabled {
      background-color: #ccc;
      cursor: not-allowed;
    }

    &:hover {
      background-color: #007bff;
    }
  }
}
</style>
