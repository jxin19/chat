import {defineStore} from 'pinia';
import axios, {AxiosError} from 'axios';
import type {Member} from "@/types/member.ts";

export const useMemberStore = defineStore('member', {
  state: () => ({
    member: null as Member | null,
    participants: [] as Member[],
  }),
  actions: {
    async login(id: number) {
      try {
        const {data} = await axios.get(`/api/member/${id}`);
        this.member = data;
      } catch (e) {
        console.error(e);
      }
    },
  }
})
