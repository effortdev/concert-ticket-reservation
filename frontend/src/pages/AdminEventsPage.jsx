import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import apiClient from '../api/client.js'

export default function AdminEventsPage() {
    const [events, setEvents] = useState([])
    const [form, setForm] = useState({ title: '', venue: '', eventDate: '', bookingOpenAt: '' })
    const [seatForms, setSeatForms] = useState({}) // eventId -> { grade, price, count }
    const [message, setMessage] = useState('')

    const loadEvents = () => {
        apiClient.get('/events').then((res) => setEvents(res.data.data))
    }

    useEffect(() => { loadEvents() }, [])

    const handleCreateEvent = async (e) => {
        e.preventDefault()
        setMessage('')
        try {
            await apiClient.post('/events', form)
            setForm({ title: '', venue: '', eventDate: '', bookingOpenAt: '' })
            setMessage('공연이 등록되었습니다.')
            loadEvents()
        } catch (err) {
            setMessage(err.response?.data?.message || '등록에 실패했습니다.')
        }
    }

    const handleSeatFormChange = (eventId, field, value) => {
        setSeatForms({
            ...seatForms,
            [eventId]: { ...(seatForms[eventId] || { grade: 'VIP', price: '', count: '' }), [field]: value },
        })
    }

    const handleGenerateSeats = async (eventId) => {
        const seatForm = seatForms[eventId]
        if (!seatForm?.price || !seatForm?.count) return
        setMessage('')
        try {
            await apiClient.post(`/events/${eventId}/seats`, {
                grade: seatForm.grade,
                price: Number(seatForm.price),
                count: Number(seatForm.count),
            })
            setMessage('좌석이 생성되었습니다.')
            setSeatForms({ ...seatForms, [eventId]: { grade: 'VIP', price: '', count: '' } })
        } catch (err) {
            setMessage(err.response?.data?.message || '좌석 생성에 실패했습니다.')
        }
    }

    return (
        <div className="min-h-screen bg-bg px-4 py-10">
            <div className="max-w-2xl mx-auto">
                <div className="flex items-center justify-between mb-8">
                    <div>
                        <p className="text-xs font-display tracking-widest text-accent mb-1">ADMIN</p>
                        <h1 className="font-display text-2xl font-semibold text-text">공연 · 좌석 관리</h1>
                    </div>
                    <Link to="/admin/dashboard" className="text-xs text-text-muted hover:text-text border border-border rounded-lg px-3 py-2 transition">
                        모니터링 화면 →
                    </Link>
                </div>

                {message && (
                    <p className="text-xs text-accent bg-accent/10 border border-accent/20 rounded-lg px-3 py-2 mb-4">
                        {message}
                    </p>
                )}

                <div className="bg-surface border border-border rounded-xl p-5 mb-6">
                    <p className="text-sm font-medium text-text mb-4">새 공연 등록</p>
                    <form onSubmit={handleCreateEvent} className="space-y-3">
                        <input
                            placeholder="공연 제목"
                            required
                            value={form.title}
                            onChange={(e) => setForm({ ...form, title: e.target.value })}
                            className="w-full bg-bg border border-border rounded-lg px-3 py-2 text-sm text-text focus:outline-none focus:border-accent"
                        />
                        <input
                            placeholder="장소"
                            required
                            value={form.venue}
                            onChange={(e) => setForm({ ...form, venue: e.target.value })}
                            className="w-full bg-bg border border-border rounded-lg px-3 py-2 text-sm text-text focus:outline-none focus:border-accent"
                        />
                        <div className="grid grid-cols-2 gap-3">
                            <div>
                                <label className="block text-xs text-text-muted mb-1">공연 일시</label>
                                <input
                                    type="datetime-local"
                                    required
                                    value={form.eventDate}
                                    onChange={(e) => setForm({ ...form, eventDate: e.target.value })}
                                    className="w-full bg-bg border border-border rounded-lg px-3 py-2 text-sm text-text focus:outline-none focus:border-accent"
                                />
                            </div>
                            <div>
                                <label className="block text-xs text-text-muted mb-1">예매 오픈</label>
                                <input
                                    type="datetime-local"
                                    required
                                    value={form.bookingOpenAt}
                                    onChange={(e) => setForm({ ...form, bookingOpenAt: e.target.value })}
                                    className="w-full bg-bg border border-border rounded-lg px-3 py-2 text-sm text-text focus:outline-none focus:border-accent"
                                />
                            </div>
                        </div>
                        <button type="submit" className="w-full bg-accent hover:bg-accent/90 text-white text-sm font-medium rounded-lg py-2.5 transition">
                            등록하기
                        </button>
                    </form>
                </div>

                <div className="space-y-3">
                    {events.map((event) => (
                        <div key={event.id} className="bg-surface border border-border rounded-xl p-5">
                            <div className="flex items-center justify-between mb-3">
                                <div>
                                    <p className="font-display font-semibold text-text">{event.title}</p>
                                    <p className="text-xs text-text-muted">{event.venue} · #{event.id}</p>
                                </div>
                                <span className="text-[10px] font-display px-2 py-0.5 rounded-full bg-amber/10 text-amber">
                  {event.status}
                </span>
                            </div>

                            <div className="flex gap-2 items-end pt-3 border-t border-border">
                                <select
                                    value={seatForms[event.id]?.grade ?? 'VIP'}
                                    onChange={(e) => handleSeatFormChange(event.id, 'grade', e.target.value)}
                                    className="bg-bg border border-border rounded-lg px-2 py-2 text-xs text-text focus:outline-none focus:border-accent"
                                >
                                    <option value="VIP">VIP</option>
                                    <option value="R">R</option>
                                    <option value="S">S</option>
                                </select>
                                <input
                                    type="number"
                                    placeholder="가격"
                                    value={seatForms[event.id]?.price ?? ''}
                                    onChange={(e) => handleSeatFormChange(event.id, 'price', e.target.value)}
                                    className="w-24 bg-bg border border-border rounded-lg px-2 py-2 text-xs text-text focus:outline-none focus:border-accent"
                                />
                                <input
                                    type="number"
                                    placeholder="수량"
                                    value={seatForms[event.id]?.count ?? ''}
                                    onChange={(e) => handleSeatFormChange(event.id, 'count', e.target.value)}
                                    className="w-20 bg-bg border border-border rounded-lg px-2 py-2 text-xs text-text focus:outline-none focus:border-accent"
                                />
                                <button
                                    onClick={() => handleGenerateSeats(event.id)}
                                    className="flex-1 bg-bg border border-border hover:border-accent text-text text-xs font-medium rounded-lg py-2 transition"
                                >
                                    좌석 생성
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}