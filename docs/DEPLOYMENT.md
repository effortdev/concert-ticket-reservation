# 배포 가이드

## 1. 서버 최초 1회 설정 (수동)

홈서버(FIREBAT R3, WSL2 + Docker)에서 딱 한 번만 하면 됨.

\`\`\`bash
git clone https://github.com/effortdev/concert-ticket-reservation.git
cd concert-ticket-reservation
\`\`\`

Nginx Proxy Manager에서:
- `ticket.본인도메인.com` → frontend 컨테이너 (포트 80)
- `ticket.본인도메인.com/api` → backend 컨테이너 (포트 8080)
- `ticket.본인도메인.com/ws` → backend 컨테이너, 웹소켓 지원(Upgrade/Connection 헤더) 활성화 필수

## 2. GitHub Actions Secrets 등록

레포 Settings → Secrets and variables → Actions → New repository secret

| Secret 이름 | 설명 |
|---|---|
| `SSH_HOST` | 홈서버 접속 주소 (공인 IP 또는 DDNS 도메인) |
| `SSH_USER` | SSH 접속 계정 |
| `SSH_KEY` | SSH 접속용 private key (전체 내용) |
| `SSH_PORT` | SSH 포트 |
| `DEPLOY_PATH` | 서버에서 레포가 clone된 경로 |
| `MYSQL_ROOT_PASSWORD` | MySQL 루트 비밀번호 |
| `JWT_SECRET` | JWT 서명용 시크릿 |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | Google OAuth 콘솔 발급값 |
| `KAKAO_CLIENT_ID` / `KAKAO_CLIENT_SECRET` | Kakao Developers 발급값 |

## 3. 배포 흐름

1. 로컬에서 개발 → 로컬 docker-compose로 테스트
2. `main` push
3. GitHub Actions가 SSH 접속 → git pull → Secrets로 .env 재생성 → docker compose 재기동
4. 서버 .env는 수동 관리 불필요 (매번 자동 생성)