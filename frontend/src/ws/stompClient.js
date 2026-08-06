import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

// 대기열 순번 실시간 알림 구독에 사용할 STOMP 클라이언트
// 사용 예정: client.subscribe(`/sub/queue/${eventId}`, callback)
export function createStompClient() {
  return new Client({
    webSocketFactory: () => new SockJS('/ws'),
    reconnectDelay: 5000,
  })
}

// TODO: 연결/해제, 구독 관리 로직 구현
