import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import apiClient from '../api/client.js'

export default function ReservationResultPage() {
    const { id } = useParams()
    const navigate = useNavigate()

    const [status, setStatus] = useState('idle') // idle | loading | success | failed
    const [errorMessage, setErrorMessage] = useState('')

    const handleConfirm = async () => {
        setStatus('loading')
        try {
            await apiClient.post(`/reservations/${id}/confirm`)
            setStatus('success')
        } catch (err) {
            setErrorMessage(err.response?.data?.message || '결제에 실패했습니다.')
            setStatus('failed')
        }
    }

    return (
        <div className="min-h-screen bg-bg flex items-center justify-center px-4">
            <div className="w-full max-w-sm text-center">

                {status === 'idle' && (
                    <>
                        <div className="w-16 h-16 rounded-2xl bg-surface border border-border flex items-center justify-center mx-auto mb-6">
                            <span className="font-display text-2xl text-accent">?</span>
                        </div>
                        <h1 className="font-display text-2xl font-semibold text-text mb-2">좌석이 홀딩되었습니다</h1>
                        <p className="text-text-muted text-sm mb-8 leading-relaxed">
                            5분 이내에 결제를 완료하지 않으면<br />좌석이 자동으로 취소됩니다
                        </p>
                        <button
                            onClick={handleConfirm}
                            className="w-full bg-accent hover:bg-accent/90 text-white text-sm font-medium rounded-lg py-3 transition"
                        >
                            결제 확정하기
                        </button>
                    </>
                )}

                {status === 'loading' && (
                    <>
                        <div className="w-16 h-16 rounded-2xl bg-surface border border-border flex items-center justify-center mx-auto mb-6 animate-pulse">
                            <span className="font-display text-2xl text-accent">···</span>
                        </div>
                        <p className="text-text-muted text-sm">결제 처리 중입니다...</p>
                    </>
                )}

                {status === 'success' && (
                    <>
                        <div className="w-16 h-16 rounded-2xl bg-emerald/10 border border-emerald/30 flex items-center justify-center mx-auto mb-6">
                            <span className="font-display text-2xl text-emerald">✓</span>
                        </div>
                        <h1 className="font-display text-2xl font-semibold text-text mb-2">예매가 확정되었습니다</h1>
                        <p className="text-text-muted text-sm mb-8">예약 번호 #{id}</p>
                        <button
                            onClick={() => navigate('/events')}
                            className="w-full bg-surface border border-border hover:bg-surface-hover text-text text-sm font-medium rounded-lg py-3 transition"
                        >
                            목록으로 돌아가기
                        </button>
                    </>
                )}

                {status === 'failed' && (
                    <>
                        <div className="w-16 h-16 rounded-2xl bg-coral/10 border border-coral/30 flex items-center justify-center mx-auto mb-6">
                            <span className="font-display text-2xl text-coral">✕</span>
                        </div>
                        <h1 className="font-display text-2xl font-semibold text-text mb-2">결제에 실패했습니다</h1>
                        <p className="text-text-muted text-sm mb-8">{errorMessage}</p>
                        <button
                            onClick={() => navigate('/events')}
                            className="w-full bg-surface border border-border hover:bg-surface-hover text-text text-sm font-medium rounded-lg py-3 transition"
                        >
                            목록으로 돌아가기
                        </button>
                    </>
                )}

            </div>
        </div>
    )
}