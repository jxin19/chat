import type {Client} from "@stomp/stompjs";
import type {Room} from "@/types/room.ts";
import type {Member} from "@/types/member.ts";

export interface ChatState {
  messages: string[]
  connected: boolean
  currentRoom: Room | null
  member: Member | null
  participants: Member[]
  error: string | null
  stompClient: Client | null
}
