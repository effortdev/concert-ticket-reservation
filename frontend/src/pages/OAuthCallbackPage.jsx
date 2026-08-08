import { useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import apiClient, {setAccessToken, setCurrentUser} from '../api/client.js'

export default function OAuthCallbackPage() {
    const [searchParams] = useSearchParams()
    const navigate = useNavigate()

    useEffect(() => {
        const accessToken = searchParams.get('accessToken')
        if (!accessToken) {
            navigate('/')
            return
        }
        setAccessToken(accessToken)
        apiClient.get('/auth/me').then((res) => {
            setCurrentUser(res.data.data)
            navigate(res.data.data.role === 'ADMIN' ? '/admin/events' : '/events')
        })
    }, [searchParams, navigate])

    return (
        <div className="min-h-screen bg-bg flex items-center justify-center">
            <p className="text-text-muted text-sm">로그인 처리 중...</p>
        </div>
    )
}