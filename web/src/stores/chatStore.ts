import {defineStore} from 'pinia'
import {Client, Stomp} from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import type {ChatState} from "@/types/chatState.ts";
import type {Message} from "@/types/message.ts";
import type {Room} from "@/types/room.ts";
import type {Member} from "@/types/member.ts";
import axios from "axios";

export const useChatStore = defineStore('chat', {
  state: (): ChatState => ({
    messages: [],
    connected: false,
    currentRoom: null,
    member: null,
    participants: [],
    error: null,
    stompClient: null
  }),
  actions: {
    setCurrentRoom(room: Room, member: Member) {
      this.currentRoom = room
      this.messages = []
      this.messages.push(`Connecting to room: ${room.name}`)
      this.member = member
      this.connectToRoom(room)
    },

    async connectToRoom(room: Room) {
      try {
        if (this.stompClient?.active) {
          await this.disconnect()
        }
        await this.connect(room.id, room.name)
      } catch (e) {
        this.error = e instanceof Error ? e.message : 'Connection failed'
      }
    },

    async disconnect() {
      if (this.stompClient?.active) {
        await this.stompClient.deactivate()
        this.connected = false
      }
    },

    async sendMessage(content: string) {
      if (!this.stompClient?.active || !this.currentRoom?.id) {
        throw new Error('Connection not established')
      }

      const message = {
        roomId: this.currentRoom?.id,
        senderMemberId: this.member?.id,
        senderMemberName: this.member?.name,
        content,
      } as Message

      try {
        await this.stompClient.publish({
          destination: `/pub/room/${this.currentRoom?.id}/send`,
          body: JSON.stringify(message)
        })
      } catch (e) {
        this.error = e instanceof Error ? e.message : 'Failed to send message'
        throw e
      }
    },

    async connect(roomId: number, roomName: string) {
      try {
        console.log('Starting connection attempt...');

        // SockJS 옵션 추가
        const socket = new SockJS(`${import.meta.env.VITE_API_URL}/ws-chat`, null, {
          transports: ['websocket', 'xhr-streaming', 'xhr-polling'],
          timeout: 10000
        });

        this.stompClient = Stomp.over(socket);

        this.stompClient = new Client({
          webSocketFactory: () => socket,
          debug: (str) => console.log('STOMP Debug:', str),
          onConnect: (frame) => {
            console.log('STOMP Connected:', frame);
            this.connected = true;
            this.messages.push(`${roomName}에 입장하셨습니다.`);
            this.updateParticipants(roomId)
            this.subscribeMessages(roomId); // 채팅방 구독 설정
            this.subscribeParticipants(roomId); // 참여자 수 구독 설정
          },
          onDisconnect: (frame) => {
            console.log('STOMP Disconnected:', frame);
            this.connected = false;
          },
          onWebSocketError: (event) => {
            console.error('WebSocket Error:', event);
          },
          onStompError: (frame) => {
            console.error('STOMP Error:', frame);
            this.error = frame.headers['message'];
          },
          reconnectDelay: 5000,
          heartbeatIncoming: 4000,
          heartbeatOutgoing: 4000
        });

        console.log('Activating STOMP client...');
        await this.stompClient.activate();
        console.log('STOMP client activation completed');
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Connection failed';
        throw error;
      }
    },

    async updateParticipants(roomId: number) {
      if (this.stompClient){
        try {
          await this.stompClient.publish({
            destination: `/pub/room/${roomId}/enter`,
            body: JSON.stringify({
              participantMemberId: this.member?.id,
              participantMemberName: this.member?.name,
            })
          })
        } catch (e) {
          this.error = e instanceof Error ? e.message : 'Failed to send message'
          throw e
        }
      }
    },

    async subscribeMessages(roomId: number) {
      this.stompClient?.subscribe(`/sub/room/${roomId}/info`, (message) => {
        try {
          const receivedMessage = JSON.parse(message.body) as Message;
          this.messages.push(`${receivedMessage.senderMemberName}: ${receivedMessage.content}`);
        } catch (e) {
          console.error('Failed to parse message:', e);
        }
      });
    },

    async subscribeParticipants(roomId: number) {
      this.stompClient?.subscribe(`/sub/room/${roomId}/participants`, (message) => {
        const participants = JSON.parse(message.body);
        this.currentRoom = {
          id: 0, maxParticipants: 0, name: "", roomType: "", status: "",
          ...this.currentRoom,
          participants: participants.list.length
        }
        this.participants = participants.list
          .map((participant: any) => ({
            id: participant.participantMemberId,
            name: participant.participantMemberName
          } as Member))
      });
    },

    async fetchLastMessagesInRoom(roomId: number, page: number) {
      const {data} = await axios.get(`/api/message/${roomId}?page=${page}`);
      const contents = data.list
        .map((message: Message) => `${message.senderMemberName}: ${message.content}`)
        .reverse();

      this.messages = [...contents, ...this.messages];
    },

    async leaveRoom() {
      if (this.stompClient && this.currentRoom){
        try {
          await this.stompClient.publish({
            destination: `/pub/room/${this.currentRoom.id}/leave`,
            body: JSON.stringify({
              participantMemberId: this.member?.id,
              participantMemberName: this.member?.name,
            })
          })
        } catch (e) {
          this.error = e instanceof Error ? e.message : 'Failed to send message'
          throw e
        }
      }
    }
  },
  getters: {
    isConnected: (state) => state.connected,
    currentMessages: (state) => state.messages,
    hasError: (state) => state.error !== null
  }
})
