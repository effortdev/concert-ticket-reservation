import axios from 'axios'

const apiClient = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

// TODO: 요청 인터셉터 - localStorage/메모리에서 accessToken 꺼내 Authorization 헤더에 추가
// TODO: 응답 인터셉터 - 401 발생 시 /api/auth/reissue 호출 후 재시도

export default apiClient
