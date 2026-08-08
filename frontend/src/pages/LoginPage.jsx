import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import apiClient, {setAccessToken, setCurrentUser} from '../api/client.js'

export default function LoginPage() {
    const navigate = useNavigate()
    const [mode, setMode] = useState('login') // 'login' | 'signup'
    const [form, setForm] = useState({ email: '', password: '', nickname: '' })
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value })
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        setError('')
        setLoading(true)

        try {
            if (mode === 'signup') {
                await apiClient.post('/auth/signup', form)
                setMode('login')
                setError('')
                setForm({ ...form, password: '' })
                return
            }

            const res = await apiClient.post('/auth/login', {
                email: form.email,
                password: form.password,
            })
            setAccessToken(res.data.data.accessToken)
            const me = await apiClient.get('/auth/me')
            setCurrentUser(me.data.data)
            navigate(me.data.data.role === 'ADMIN' ? '/admin/events' : '/events')
        } catch (err) {
            const message = err.response?.data?.message || '문제가 발생했습니다. 다시 시도해주세요.'
            setError(message)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="min-h-screen bg-bg flex items-center justify-center px-4">
            <div className="w-full max-w-sm">
                <div className="text-center mb-8">
                    <div className="inline-block px-3 py-1 rounded-full bg-surface border border-border text-xs text-accent font-medium mb-4 font-display tracking-wide">
                        LIVE TICKETING
                    </div>
                    <h1 className="font-display text-3xl font-semibold text-text">
                        {mode === 'login' ? '다시 만나서 반가워요' : '계정을 만들어주세요'}
                    </h1>
                </div>

                <div className="bg-surface border border-border rounded-2xl p-6">
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label className="block text-xs font-medium text-text-muted mb-1.5">이메일</label>
                            <input
                                type="email"
                                name="email"
                                required
                                value={form.email}
                                onChange={handleChange}
                                className="w-full bg-bg border border-border rounded-lg px-3.5 py-2.5 text-sm text-text placeholder:text-text-muted/50 focus:outline-none focus:ring-2 focus:ring-accent/50 focus:border-accent transition"
                                placeholder="you@example.com"
                            />
                        </div>

                        {mode === 'signup' && (
                            <div>
                                <label className="block text-xs font-medium text-text-muted mb-1.5">닉네임</label>
                                <input
                                    type="text"
                                    name="nickname"
                                    required
                                    value={form.nickname}
                                    onChange={handleChange}
                                    className="w-full bg-bg border border-border rounded-lg px-3.5 py-2.5 text-sm text-text placeholder:text-text-muted/50 focus:outline-none focus:ring-2 focus:ring-accent/50 focus:border-accent transition"
                                    placeholder="닉네임"
                                />
                            </div>
                        )}

                        <div>
                            <label className="block text-xs font-medium text-text-muted mb-1.5">비밀번호</label>
                            <input
                                type="password"
                                name="password"
                                required
                                minLength={8}
                                value={form.password}
                                onChange={handleChange}
                                className="w-full bg-bg border border-border rounded-lg px-3.5 py-2.5 text-sm text-text placeholder:text-text-muted/50 focus:outline-none focus:ring-2 focus:ring-accent/50 focus:border-accent transition"
                                placeholder="8자 이상"
                            />
                        </div>

                        {error && (
                            <p className="text-coral text-xs bg-coral/10 border border-coral/20 rounded-lg px-3 py-2">
                                {error}
                            </p>
                        )}

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full bg-accent hover:bg-accent/90 disabled:opacity-50 text-white text-sm font-medium rounded-lg py-2.5 transition"
                        >
                            {loading ? '처리 중...' : mode === 'login' ? '로그인' : '가입하기'}
                        </button>
                    </form>

                    <div className="flex items-center gap-3 my-5">
                        <div className="flex-1 h-px bg-border" />
                        <span className="text-xs text-text-muted">또는</span>
                        <div className="flex-1 h-px bg-border" />
                    </div>


                    <a href="/oauth2/authorization/google"
                    className="w-full flex items-center justify-center gap-2 bg-bg border border-border hover:bg-surface-hover text-text text-sm font-medium rounded-lg py-2.5 transition"
                    >
                    <svg width="16" height="16" viewBox="0 0 24 24">
                        <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                        <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                        <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                        <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                    </svg>
                    Google로 계속하기
                </a>

                <button
                    onClick={() => {
                        setMode(mode === 'login' ? 'signup' : 'login')
                        setError('')
                    }}
                    className="w-full text-center text-xs text-text-muted hover:text-text mt-5 transition"
                >
                    {mode === 'login' ? '계정이 없으신가요? 가입하기' : '이미 계정이 있으신가요? 로그인'}
                </button>
            </div>
        </div>
</div>
)
}