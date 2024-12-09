<template>
  <Teleport to="body">
    <div v-if="isOpen" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>로그인</h3>
          <button class="close-button" @click="closeModal">&times;</button>
        </div>

        <div class="modal-body">
          <div class="input-group">
            <label>회원 번호</label>
            <input
              type="number"
              v-model="memberId"
              placeholder="회원 번호를 입력하세요"
              @keyup.enter="handleLogin"
            >
          </div>

          <div v-if="error" class="error-message">
            {{ error }}
          </div>
        </div>

        <div class="modal-footer">
          <button
            class="login-button"
            @click="handleLogin"
            :disabled="!memberId"
          >
            로그인
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useMemberStore } from '@/stores/memberStore'
import type {Member} from "@/types/member.ts";

const props = defineProps<{
  isOpen: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'login', member: Member): void
}>()

const memberStore = useMemberStore()
const memberId = ref<number | null>(null)
const error = ref('')

const closeModal = () => {
  emit('close')
  memberId.value = null
  error.value = ''
}

const handleLogin = async () => {
  if (!memberId.value) {
    error.value = '회원 번호를 입력해주세요.'
    return
  }

  try {
    await memberStore.login(memberId.value)
    if (memberStore.member) {
      emit('login', memberStore.member)
      closeModal()
    } else {
      error.value = '존재하지 않는 회원번호입니다.'
    }
  } catch (e) {
    error.value = '로그인 중 오류가 발생했습니다.'
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  width: 400px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.modal-header {
  padding: 1rem;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

.modal-body {
  padding: 1.5rem;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.input-group label {
  font-weight: 500;
}

.input-group input {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.error-message {
  color: #dc3545;
  font-size: 0.875rem;
  margin-top: 0.5rem;
}

.modal-footer {
  padding: 1rem;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
}

.login-button {
  padding: 0.5rem 1.5rem;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.login-button:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>
