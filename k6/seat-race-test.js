import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'https://concert-ticketing.duckdns.org';
const EVENT_ID = __ENV.EVENT_ID;
const SEAT_ID = __ENV.SEAT_ID;
const VU_COUNT = 50;

export const options = {
    vus: VU_COUNT,
    iterations: VU_COUNT,
};

export function setup() {
    const tokens = [];

    for (let i = 1; i <= VU_COUNT; i++) {
        const loginRes = http.post(
            `${BASE_URL}/api/auth/login`,
            JSON.stringify({ email: `loadtest_user_${i}@test.com`, password: '12345678' }),
            { headers: { 'Content-Type': 'application/json' } }
        );

        const token = loginRes.json('data.accessToken');
        tokens.push(token);

        http.post(`${BASE_URL}/api/queue/${EVENT_ID}/enter`, null, {
            headers: { Authorization: `Bearer ${token}` },
        });
    }

    sleep(6);

    for (const token of tokens) {
        http.post(`${BASE_URL}/api/queue/${EVENT_ID}/confirm-entry`, null, {
            headers: { Authorization: `Bearer ${token}` },
        });
    }

    return { tokens };
}

export default function (data) {
    const token = data.tokens[__VU - 1];

    const res = http.post(
        `${BASE_URL}/api/reservations/hold`,
        JSON.stringify({ eventId: Number(EVENT_ID), seatId: Number(SEAT_ID) }),
        { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
    );

    check(res, {
        'got response': (r) => r.status === 200 || r.status === 409 || r.status === 429,
        'success (200)': (r) => r.status === 200,
        'conflict (409)': (r) => r.status === 409,
    });
}