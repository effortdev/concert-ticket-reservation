import { useNavigate } from 'react-router-dom'
import { getCurrentUser, logout } from '../api/client.js'

export default function Header() {
    const navigate = useNavigate()
    const user = getCurrentUser()

    const handleLogout = () => {
        logout()
        navigate('/')
    }

    if (!user) return null

    return (
        <div className="flex items-center justify-between max-w-2xl mx-auto px-4 pt-6 mb-2">
      <span className="text-xs text-text-muted">
        {user.nickname}
          {user.role === 'ADMIN' && <span className="ml-1.5 text-accent">· ADMIN</span>}
      </span>
            <button
                onClick={handleLogout}
                className="text-xs text-text-muted hover:text-coral transition"
            >
                로그아웃
            </button>
        </div>
    )
}