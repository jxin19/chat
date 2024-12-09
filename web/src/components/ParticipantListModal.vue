<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <div class="modal-header">
        <h3>채팅방 참여자</h3>
        <button class="close-button" @click="handleCloseModal">&times;</button>
      </div>
      <div class="modal-body">
        <ul class="participants-list">
          <li v-for="participant in participants" :key="participant.id" class="participant-item">
            <div class="participant-info">
              <span class="participant-name">{{ participant.name }}</span>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {useChatStore} from "@/stores/chatStore.ts";
import {computed} from "vue";

const chatStore = useChatStore()
const participants = computed(() => chatStore.participants)

const handleCloseModal = () => {
  emit('handleCloseModal')
}

const emit = defineEmits<{
  (e: 'handleCloseModal'): void
}>()
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #ddd;
}

.modal-header h3 {
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
}

.modal-body {
  padding: 20px;
}

.participants-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.participant-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.participant-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.participant-name {
  font-weight: 500;
}
</style>
