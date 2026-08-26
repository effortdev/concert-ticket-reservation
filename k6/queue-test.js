import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = 'https://concert-ticketing.duckdns.org';
const EVENT_ID = __ENV.EVENT_ID; // 실행 시 -e EVENT_ID=숫자 로 전달

export const options = {
    vus: 100,
    iterations: 100, // 각 VU가 1번씩만 실행 -> 총 100명이 동시에 1회
};

export default function () {
    const userIndex = (__VU - 1) % 100 + 1; // VU 번호를 1~100 유저에 매핑
    const email = `loadtest_user_${userIndex}@test.com`;

    // 1. 로그인
    const loginRes = http.post(
        `${BASE_URL}/api/auth/login`,
        JSON.stringify({ email: email, password: '12345678' }),
        { headers: { 'Content-Type': 'application/json' } }
    );

    check(loginRes, { 'login success': (r) => r.status === 200 });

    const accessToken = loginRes.json('data.accessToken');

    // 2. 대기열 진입
    const enterRes = http.post(
        `${BASE_URL}/api/queue/${EVENT_ID}/enter`,
        null,
        { headers: { Authorization: `Bearer ${accessToken}` } }
    );

    check(enterRes, {
        'queue enter success': (r) => r.status === 200,
        'has valid rank': (r) => r.json('data.rank') > 0,
    });
}