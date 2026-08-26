import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = 'https://concert-ticketing.duckdns.org';
const USER_COUNT = 100;

export const options = {
    vus: 1,
    iterations: 1,
};

export default function () {
    for (let i = 1; i <= USER_COUNT; i++) {
        const email = `loadtest_user_${i}@test.com`;
        const res = http.post(
            `${BASE_URL}/api/auth/signup`,
            JSON.stringify({
                email: email,
                password: '12345678',
                nickname: `부하테스트${i}`,
            }),
            { headers: { 'Content-Type': 'application/json' } }
        );

        check(res, {
            [`user ${i} created or already exists`]: (r) => r.status === 201 || r.status === 409,
        });
    }
}