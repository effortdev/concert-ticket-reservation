import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import apiClient from '../api/client.js'

export default function EventListPage() {
    const navigate = useNavigate()
    const [events, setEvents] = useState([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        apiClient.get('/events')
            .then((res) => setEvents(res.data.data))
            .finally(() => setLoading(false))
    }, [])

    const formatDate = (iso) => {
        const d = new Date(iso)
        return `${d.getMonth() + 1}.${d.getDate()} ${d.getHours()}:${String(d.getMinutes()).padStart(2, '0')}`
    }

    return (
        <div className="min-h-screen bg-bg px-4 py-10">
            <div className="max-w-md mx-auto">
                <h1 className="font-display text-2xl font-semibold text-text mb-1">예매 가능한 공연</h1>
                <p className="text-text-muted text-sm mb-8">원하는 공연을 선택하면 대기열에 진입합니다</p>

                {loading && <p className="text-text-muted text-sm">불러오는 중...</p>}

                <div className="space-y-3">
                    {events.map((event) => (
                        <button
                            key={event.id}
                            onClick={() => navigate(`/queue/${event.id}`)}
                            className="w-full text-left bg-surface border border-border hover:border-accent/50 rounded-xl p-4 transition group"
                        >
                            <div className="flex items-center justify-between mb-2">
                <span className={`text-[10px] font-medium px-2 py-0.5 rounded-full font-display tracking-wide ${
                    event.status === 'SCHEDULED'
                        ? 'bg-amber/10 text-amber'
                        : 'bg-emerald/10 text-emerald'
                }`}>
                  {event.status === 'SCHEDULED' ? 'OPEN SOON' : event.status}
                </span>
                                <span className="text-xs text-text-muted">{formatDate(event.eventDate)}</span>
                            </div>
                            <h2 className="font-display text-lg font-semibold text-text group-hover:text-accent transition">
                                {event.title}
                            </h2>
                            <p className="text-text-muted text-sm mt-0.5">{event.venue}</p>
                        </button>
                    ))}
                </div>

                {!loading && events.length === 0 && (
                    <p className="text-text-muted text-sm text-center py-12">등록된 공연이 없습니다.</p>
                )}
            </div>
        </div>
    )
}