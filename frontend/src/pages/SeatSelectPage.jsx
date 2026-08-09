import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import apiClient from '../api/client.js'
import Header from '../components/Header.jsx'

const GRADE_ORDER = ['VIP', 'R', 'S']
const GRADE_COLOR = {
    VIP: 'border-accent text-accent',
    R: 'border-amber text-amber',
    S: 'border-emerald text-emerald',
}

export default function SeatSelectPage() {
    const { eventId } = useParams()
    const navigate = useNavigate()

    const [seats, setSeats] = useState([])
    const [selectingId, setSelectingId] = useState(null)
    const [error, setError] = useState('')

    const loadSeats = () => {
        apiClient.get(`/events/${eventId}/seats`).then((res) => setSeats(res.data.data))
    }

    useEffect(() => {
        loadSeats()
    }, [eventId])

    const handleSelect = async (seat) => {
        if (seat.status !== 'AVAILABLE') return
        setError('')
        setSelectingId(seat.id)

        try {
            const res = await apiClient.post('/reservations/hold', {
                eventId: Number(eventId),
                seatId: seat.id,
            })
            navigate(`/reservations/${res.data.data.reservationId}/result`)
        } catch (err) {
            const message = err.response?.data?.message || '좌석 선택에 실패했습니다.'
            setError(message)
            loadSeats() // 다른 사람이 방금 선점했을 수 있으니 최신 상태로 갱신
        } finally {
            setSelectingId(null)
        }
    }

    const grouped = GRADE_ORDER.map((grade) => ({
        grade,
        seats: seats.filter((s) => s.grade === grade),
    })).filter((g) => g.seats.length > 0)

    return (
        <div className="min-h-screen bg-bg px-4 py-10">
            <Header />
            <div className="max-w-md mx-auto">
                <h1 className="font-display text-2xl font-semibold text-text mb-1">좌석을 선택하세요</h1>
                <p className="text-text-muted text-sm mb-6">선택 즉시 5분간 임시 예약됩니다</p>

                {error && (
                    <p className="text-coral text-xs bg-coral/10 border border-coral/20 rounded-lg px-3 py-2 mb-4">
                        {error}
                    </p>
                )}

                <div className="flex gap-4 mb-6 text-xs text-text-muted">
          <span className="flex items-center gap-1.5">
            <span className="w-3 h-3 rounded border border-border bg-surface inline-block" /> 선택 가능
          </span>
                    <span className="flex items-center gap-1.5">
            <span className="w-3 h-3 rounded bg-border inline-block" /> 선택 불가
          </span>
                </div>

                <div className="space-y-6">
                    {grouped.map(({ grade, seats }) => (
                        <div key={grade}>
                            <div className={`inline-block text-xs font-display font-semibold tracking-wide border rounded-full px-2.5 py-0.5 mb-3 ${GRADE_COLOR[grade]}`}>
                                {grade}
                            </div>
                            <div className="grid grid-cols-4 gap-2">
                                {seats.map((seat) => {
                                    const isAvailable = seat.status === 'AVAILABLE'
                                    const isLoading = selectingId === seat.id
                                    return (
                                        <button
                                            key={seat.id}
                                            disabled={!isAvailable || isLoading}
                                            onClick={() => handleSelect(seat)}
                                            className={`aspect-square rounded-lg text-xs font-medium transition flex flex-col items-center justify-center gap-0.5
                        ${isAvailable
                                                ? 'bg-surface border border-border hover:border-accent hover:bg-surface-hover text-text cursor-pointer'
                                                : 'bg-border/40 text-text-muted/50 cursor-not-allowed'}
                        ${isLoading ? 'opacity-50 animate-pulse' : ''}
                      `}
                                        >
                                            <span>{seat.seatNumber}</span>
                                            {isAvailable && <span className="text-[10px] text-text-muted">{seat.price.toLocaleString()}원</span>}
                                        </button>
                                    )
                                })}
                            </div>
                        </div>
                    ))}
                </div>

                {seats.length === 0 && (
                    <p className="text-text-muted text-sm text-center py-12">등록된 좌석이 없습니다.</p>
                )}
            </div>
        </div>
    )
}