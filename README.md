# 실시간 대기열 기반 콘서트 티켓 예매 시스템

동시 접속 폭주 상황을 Redis 기반 대기열로 제어하고, 좌석 동시 선점 문제를 분산 락(Redisson)으로 해결하는 백엔드 포트폴리오 프로젝트.

## 기술 스택

**Backend**: Spring Boot 3.3, Java 17, Spring Security, Spring Data JPA, MySQL
**동시성/대기열**: Redis (Sorted Set, TTL), Redisson (분산 락)
**실시간 통신**: WebSocket (STOMP)
**인증**: JWT, OAuth2 (Google, Kakao)
**Frontend**: React 18, Vite, Tailwind CSS
**인프라**: Docker, Docker Compose, GitHub Actions (CI/CD)

## 핵심 기능

- [ ] JWT + OAuth2 로그인
- [ ] 대기열 진입 및 순번 관리 (Redis Sorted Set)
- [ ] 웹소켓 기반 순번 도달 알림
- [ ] 좌석 임시 홀딩 (Redis TTL)
- [ ] 좌석 동시 선점 방지 (Redisson 분산 락)
- [ ] Mock 결제 및 예매 확정
- [ ] 결과 알림 (이메일)

## 로컬 개발 환경 실행

```bash
# 1. MySQL / Redis 컨테이너 실행
docker compose up -d

# 2. 백엔드 실행 (IntelliJ 또는)
cd backend
./gradlew bootRun

# 3. 프론트엔드 실행
cd frontend
npm install
npm run dev
```

## 프로젝트 구조

```
backend/
  src/main/java/com/effortdev/ticketing/
    common/         공통 응답, 예외 처리
    config/         Security, Redis, WebSocket 설정
    domain/         User, Event, Seat, Reservation, Queue
    controller/     REST API
    security/       JWT, OAuth
    websocket/       실시간 알림

frontend/
  src/
    pages/          로그인, 대기열, 좌석선택, 결과 화면
    components/     공용 컴포넌트
    api/            axios 클라이언트
    ws/             STOMP 웹소켓 클라이언트
```

## 트러블슈팅

(진행하면서 마주친 문제와 해결 과정을 기록 예정)
