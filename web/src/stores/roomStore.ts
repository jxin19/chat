import {defineStore} from 'pinia';
import axios, {AxiosError} from 'axios';
import type {Room} from "@/types/room.ts";

export const useRoomStore = defineStore('room', {
  state: () => ({
    list: [] as Room[],
  }),
  actions: {
    async fetchList() {
      try {
        const {data} = await axios.get('/api/room/list');
        this.list = data.list as Room[];
      } catch (e) {
        console.error(e);
      }
    },
  }
})
