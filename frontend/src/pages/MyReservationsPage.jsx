import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import apiClient from '../api/client.js'
import Header from '../components/Header.jsx'

const STATUS_LABEL = {
    HOLDING: { text: '결제 대기중', color: 'text-amber' },
    CONFIRMED: { text: '예매 확정', color: 'text-emerald' },
    CANCELED: { text: '취소됨', color: 'text-text-muted' },
    EXPIRED: { text: '만료됨', color: 'text-coral' },
}

export default function MyReservationsPage() {
    const navigate = useNavigate()
    const [reservations, setReservations] = useState([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        apiClient.get('/reservations/my')
            .then((res) => setReservations(res.data.data))
            .finally(() => setLoading(false))
    }, [])

    const formatDate = (iso) => {
        const d = new Date(iso)
        return `${d.getMonth() + 1}.${d.getDate()} ${d.getHours()}:${String(d.getMinutes()).padStart(2, '0')}`
    }

    return (
        <div className="min-h-screen bg-bg px-4 py-10">
            <Header />
            <div className="max-w-md mx-auto">
                <h1 className="font-display text-2xl font-semibold text-text mb-1">내 예매 내역</h1>
                <p className="text-text-muted text-sm mb-8">지금까지 신청한 예매 목록입니다</p>

                {loading && <p className="text-text-muted text-sm">불러오는 중...</p>}

                <div className="space-y-3">
                    {reservations.map((r) => {
                        const status = STATUS_LABEL[r.status] ?? { text: r.status, color: 'text-text-muted' }
                        return (
                            <div key={r.reservationId} className="bg-surface border border-border rounded-xl p-4">
                                <div className="flex items-center justify-between mb-1">
                                    <span className={`text-xs font-display font-medium ${status.color}`}>{status.text}</span>
                                    <span className="text-xs text-text-muted">{formatDate(r.createdAt)}</span>
                                </div>
                                <p className="text-sm text-text">예약번호 #{r.reservationId} · 좌석 #{r.seatId}</p>

                                {r.status === 'HOLDING' && (
                                    <button
                                        onClick={() => navigate(`/reservations/${r.reservationId}/result`)}
                                        className="mt-3 w-full bg-accent hover:bg-accent/90 text-white text-xs font-medium rounded-lg py-2 transition"
                                    >
                                        결제 이어하기
                                    </button>
                                )}
                            </div>
                        )
                    })}
                </div>

                {!loading && reservations.length === 0 && (
                    <p className="text-text-muted text-sm text-center py-12">예매 내역이 없습니다.</p>
                )}
            </div>
        </div>
    )
}