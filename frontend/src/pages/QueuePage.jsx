import { useEffect, useRef, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import apiClient from '../api/client.js'

export default function QueuePage() {
    const { eventId } = useParams()
    const navigate = useNavigate()

    const [myRank, setMyRank] = useState(null)
    const [allowedRank, setAllowedRank] = useState(0)
    const [queueSize, setQueueSize] = useState(0)
    const [status, setStatus] = useState('연결 중')
    const enteredRef = useRef(false)

    useEffect(() => {
        let stompClient

        async function start() {
            // 1. 대기열 진입
            const enterRes = await apiClient.post(`/queue/${eventId}/enter`)
            setMyRank(enterRes.data.data.rank)

            // 2. 현재 허용 순번 초기 조회
            const allowedRes = await apiClient.get(`/queue/${eventId}/allowed`)
            setAllowedRank(allowedRes.data.data)
            setStatus('대기 중')

            // 3. 웹소켓 구독 - 허용 순번이 바뀔 때마다 실시간 갱신
            stompClient = new Client({
                webSocketFactory: () => new SockJS('/ws'),
                reconnectDelay: 3000,
                onConnect: () => {
                    stompClient.subscribe(`/sub/queue/${eventId}`, (message) => {
                        const data = JSON.parse(message.body)
                        setAllowedRank(data.allowedRank)
                        setQueueSize(data.queueSize)
                    })
                },
            })
            stompClient.activate()
        }

        start()
        return () => stompClient?.deactivate()
    }, [eventId])

    // 내 순번이 허용 범위에 들어오면 자동 입장 처리
    useEffect(() => {
        if (myRank === null || enteredRef.current) return
        if (myRank <= allowedRank && allowedRank > 0) {
            enteredRef.current = true
            setStatus('입장 처리 중')
            apiClient.post(`/queue/${eventId}/confirm-entry`)
                .then(() => navigate(`/events/${eventId}/seats`))
                .catch(() => {
                    enteredRef.current = false
                    setStatus('입장 실패, 재시도 중')
                })
        }
    }, [myRank, allowedRank, eventId, navigate])

    const progress = myRank ? Math.min(100, (allowedRank / myRank) * 100) : 0

    return (
        <div className="min-h-screen bg-bg flex items-center justify-center px-4">
            <div className="w-full max-w-sm text-center">
                <p className="text-xs font-display tracking-widest text-text-muted mb-3">
                    {status.toUpperCase()}
                </p>

                <div className="bg-surface border border-border rounded-3xl py-12 px-6 mb-6">
                    <p className="text-text-muted text-xs mb-2">나의 대기 순번</p>
                    <div className="font-display text-7xl font-bold text-text tabular-nums leading-none">
                        {myRank ?? '—'}
                    </div>

                    <div className="mt-8 h-1.5 bg-bg rounded-full overflow-hidden">
                        <div
                            className="h-full bg-accent transition-all duration-700 ease-out"
                            style={{ width: `${progress}%` }}
                        />
                    </div>

                    <div className="flex justify-between mt-3 text-xs text-text-muted">
                        <span>현재 입장 허용: <span className="text-amber font-medium tabular-nums">{allowedRank}</span>번</span>
                        {queueSize > 0 && <span>전체 <span className="tabular-nums">{queueSize}</span>명</span>}
                    </div>
                </div>

                <p className="text-text-muted text-xs leading-relaxed">
                    곧 입장하실 수 있어요. 이 화면을 벗어나지 마세요.
                </p>
            </div>
        </div>
    )
}