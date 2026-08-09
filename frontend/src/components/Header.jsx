import { useNavigate, useLocation } from 'react-router-dom'
import { getCurrentUser, logout } from '../api/client.js'

export default function Header() {
    const navigate = useNavigate()
    const location = useLocation()
    const user = getCurrentUser()

    const handleLogout = () => {
        logout()
        navigate('/')
    }

    if (!user) return null

    const isAdmin = user.role === 'ADMIN'

    return (
        <div className="flex items-center justify-between max-w-2xl mx-auto px-4 pt-6 mb-2">
            <div className="flex items-center gap-3">
        <span className="text-xs text-text-muted">
          {user.nickname}
            {isAdmin && <span className="ml-1.5 text-accent">· ADMIN</span>}
        </span>

                {isAdmin && (
                    <div className="flex items-center gap-2 text-xs">
                        {location.pathname !== '/admin/events' && (
                            <button onClick={() => navigate('/admin/events')} className="text-text-muted hover:text-text transition">
                                공연·좌석 관리
                            </button>
                        )}
                        {location.pathname !== '/admin/dashboard' && (
                            <button onClick={() => navigate('/admin/dashboard')} className="text-text-muted hover:text-text transition">
                                모니터링
                            </button>
                        )}
                    </div>
                )}

                {!isAdmin && location.pathname !== '/events' && (
                    <button onClick={() => navigate('/events')} className="text-xs text-text-muted hover:text-text transition">
                        공연 목록
                    </button>
                )}

                {!isAdmin && location.pathname !== '/reservations/my' && (
                    <button onClick={() => navigate('/reservations/my')} className="text-xs text-text-muted hover:text-text transition">
                        내 예매
                    </button>
                )}
            </div>

            <button onClick={handleLogout} className="text-xs text-text-muted hover:text-coral transition">
                로그아웃
            </button>
        </div>
    )
}