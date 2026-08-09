import { useEffect, useRef, useState } from 'react'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import apiClient from '../api/client.js'
import Header from '../components/Header.jsx'

export default function AdminDashboardPage() {
    const [events, setEvents] = useState([])
    const [eventId, setEventId] = useState(null)

    const [queueSize, setQueueSize] = useState(0)
    const [allowedRank, setAllowedRank] = useState(0)
    const [speed, setSpeed] = useState(0) // 초당 처리 인원
    const [seatStats, setSeatStats] = useState({ AVAILABLE: 0, HOLDING: 0, SOLD: 0 })

    const lastRef = useRef({ rank: 0, time: null })

    useEffect(() => {
        apiClient.get('/events').then((res) => {
            setEvents(res.data.data)
            if (res.data.data.length > 0) setEventId(res.data.data[0].id)
        })
    }, [])

    // 웹소켓 구독 - 대기열 실시간 방송
    useEffect(() => {
        if (!eventId) return

        const stompClient = new Client({
            webSocketFactory: () => new SockJS('/ws'),
            reconnectDelay: 3000,
            onConnect: () => {
                stompClient.subscribe(`/sub/queue/${eventId}`, (message) => {
                    const data = JSON.parse(message.body)
                    const now = Date.now()

                    if (lastRef.current.time) {
                        const deltaRank = data.allowedRank - lastRef.current.rank
                        const deltaSec = (now - lastRef.current.time) / 1000
                        if (deltaSec > 0) setSpeed(Math.round((deltaRank / deltaSec) * 10) / 10)
                    }
                    lastRef.current = { rank: data.allowedRank, time: now }

                    setAllowedRank(data.allowedRank)
                    setQueueSize(data.queueSize)
                })
            },
        })
        stompClient.activate()
        return () => stompClient.deactivate()
    }, [eventId])

    // 좌석 현황 폴링
    useEffect(() => {
        if (!eventId) return

        const fetchSeats = () => {
            apiClient.get(`/events/${eventId}/seats`).then((res) => {
                const counts = { AVAILABLE: 0, HOLDING: 0, SOLD: 0 }
                res.data.data.forEach((s) => { counts[s.status] = (counts[s.status] || 0) + 1 })
                setSeatStats(counts)
            })
        }

        fetchSeats()
        const interval = setInterval(fetchSeats, 3000)
        return () => clearInterval(interval)
    }, [eventId])

    const totalSeats = seatStats.AVAILABLE + seatStats.HOLDING + seatStats.SOLD
    const soldRatio = totalSeats ? (seatStats.SOLD / totalSeats) * 100 : 0

    return (
        <div className="min-h-screen bg-bg px-4 py-10">
            <Header />
            <div className="max-w-2xl mx-auto">
                <div className="flex items-center justify-between mb-8">
                    <div>
                        <p className="text-xs font-display tracking-widest text-accent mb-1">ADMIN</p>
                        <h1 className="font-display text-2xl font-semibold text-text">실시간 모니터링</h1>
                    </div>
                    <select
                        value={eventId ?? ''}
                        onChange={(e) => setEventId(Number(e.target.value))}
                        className="bg-surface border border-border rounded-lg px-3 py-2 text-sm text-text focus:outline-none focus:border-accent"
                    >
                        {events.map((e) => (
                            <option key={e.id} value={e.id}>{e.title}</option>
                        ))}
                    </select>
                </div>

                <div className="grid grid-cols-3 gap-3 mb-4">
                    <div className="bg-surface border border-border rounded-xl p-5">
                        <p className="text-xs text-text-muted mb-2">전체 대기 인원</p>
                        <p className="font-display text-3xl font-bold text-text tabular-nums">{queueSize}</p>
                    </div>
                    <div className="bg-surface border border-border rounded-xl p-5">
                        <p className="text-xs text-text-muted mb-2">현재 입장 허용</p>
                        <p className="font-display text-3xl font-bold text-amber tabular-nums">{allowedRank}</p>
                    </div>
                    <div className="bg-surface border border-border rounded-xl p-5">
                        <p className="text-xs text-text-muted mb-2">처리 속도</p>
                        <p className="font-display text-3xl font-bold text-accent tabular-nums">
                            {speed}<span className="text-sm text-text-muted ml-1">명/초</span>
                        </p>
                    </div>
                </div>

                <div className="bg-surface border border-border rounded-xl p-5">
                    <div className="flex items-center justify-between mb-3">
                        <p className="text-xs text-text-muted">좌석 판매 현황</p>
                        <p className="text-xs text-text-muted tabular-nums">{seatStats.SOLD} / {totalSeats}석 판매</p>
                    </div>

                    <div className="h-3 bg-bg rounded-full overflow-hidden flex">
                        <div className="bg-emerald h-full transition-all duration-500" style={{ width: `${soldRatio}%` }} />
                        <div
                            className="bg-amber h-full transition-all duration-500"
                            style={{ width: `${totalSeats ? (seatStats.HOLDING / totalSeats) * 100 : 0}%` }}
                        />
                    </div>

                    <div className="flex gap-4 mt-3 text-xs text-text-muted">
                        <span className="flex items-center gap-1.5"><span className="w-2 h-2 rounded-full bg-emerald inline-block" /> 판매완료 {seatStats.SOLD}</span>
                        <span className="flex items-center gap-1.5"><span className="w-2 h-2 rounded-full bg-amber inline-block" /> 홀딩중 {seatStats.HOLDING}</span>
                        <span className="flex items-center gap-1.5"><span className="w-2 h-2 rounded-full bg-border inline-block" /> 잔여 {seatStats.AVAILABLE}</span>
                    </div>
                </div>
            </div>
        </div>
    )
}