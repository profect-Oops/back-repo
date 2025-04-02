
## 웹 서비스 아키텍처

웹 서비스는 클라이언트(프론트엔드), 서버(백엔드), 데이터베이스(DB)로 구성됩니다. 실시간 데이터를 처리하고, 빠른 응답 속도를 보장하기 위해 WebSocket, Redis 캐싱, Redis Pub/Sub을 활용한 실시간 이벤트 처리를 적용하였습니다.

<img width="368" alt="스크린샷 2025-02-26 오후 8 44 53" src="https://github.com/user-attachments/assets/bf5335e5-647a-4067-8064-184c6f020e77" />


### 📌 Architecture 구성

#### Client Tier
- 사용자 인터페이스(UI) 구성
- HTML, CSS, JavaScript 기반
- API 요청을 통해 백엔드와 통신

#### Application Tier
- 백엔드 서버의 비즈니스 로직 처리
- 데이터베이스와 상호작용
- API 제공, 인증, 데이터 가공 역할 수행

#### Data Tier
- 데이터베이스 운영
- SQL 기반 데이터 저장, 수정, 조회 수행
- Redis 캐시 및 메시지 브로커 활용

### 🌐 전체 흐름

<img width="705" alt="스크린샷 2025-02-27 오전 9 48 19" src="https://github.com/user-attachments/assets/87fc2599-55db-4dff-9058-850b22ecd918" />

사용자가 `https://todaycoinfo.com`에 접속하면, **CloudFront**가 **S3**에 저장된 정적 파일(HTML, CSS, JavaScript 등)을 제공하여 화면을 렌더링합니다.  
이후 **API 또는 WebSocket 요청**이 발생하면, 해당 요청은 `https://api.todaycoinfo.com`을 통해 전달됩니다.  
이 요청은 **NLB**를 거쳐, **Kubernetes 클러스터** 안의 **NginX Ingress Controller**로 전달되고, **Ingress 설정**에 따라 적절한 **Spring Boot 서버(Pod)**로 연결됩니다.  
서버는 요청을 처리한 후 결과를 클라이언트에 반환하여, **실시간 데이터 기반의 정보 제공**이 이루어집니다.

---

## 서비스 페이지 흐름도

<img width="699" alt="스크린샷 2025-02-26 오후 2 11 55" src="https://github.com/user-attachments/assets/2e30929b-8e92-4386-a41c-ad94cd5351cb" />
<img width="671" alt="스크린샷 2025-04-01 오후 3 41 09" src="https://github.com/user-attachments/assets/edb1c46c-3be2-4171-b999-fad7f7158e5c" />


---

## 주요 화면 기능

### 1. 코인 목록 페이지 (메인)
- 초기 접속 페이지
- 개별 코인 선택 시 상세 페이지 이동

### 2. 코인 상세 페이지
- 개별 코인 정보 제공
  - 코인 기본 정보
  - 뉴스
  - 차트
  - 감성 분석 결과 등

### 3. 로그인 페이지
- 알림 서비스 이용 시 필요
- **Google 소셜 로그인** 사용

### 4. 알림 설정 페이지
- 로그인 후 사용 가능
- 알림 등록 및 해제, 관리 기능 제공

---

## 기술 스택 및 기능 설계

<img width="561" alt="스크린샷 2025-04-02 오후 1 44 28" src="https://github.com/user-attachments/assets/d6fd6431-b21f-41fc-a5d7-e7c84d6da42c" />

### 1. Upbit 연동 (시세 데이터 수집)
- WebSocket을 통한 **실시간 시세 및 캔들 데이터 수집**
- REST API를 통해 **과거 차트 데이터 수집**

### 2. Spring 서버 (중앙 데이터 처리 및 API 제공)
- WebSocket을 활용한 **실시간 데이터 전송**
- API 요청을 통한 **과거 데이터 제공**

### 3. Redis (캐싱 시스템)
- 과거 데이터를 캐싱하여 **응답 속도 최적화**

### 4. Redis Pub/Sub (알림 시스템)
- **실시간 가격 변동**을 Redis Pub/Sub으로 전송
- 알림 기준 및 알림 이력을 Redis에 캐싱하여 **DB 부하 분산**
- **조건 충족 시 이메일 알림 발송**
- TTL을 이용하여 **5분 내 중복 발송 방지**
- 알림 발송 후 자동 비활성화

### 5. JavaScript (프론트엔드)
- WebSocket을 통해 실시간 데이터 수신
- 클라이언트에서 실시간 데이터 렌더링

---

## 테스트 코드

<img width="1056" alt="스크린샷 2025-03-18 오후 2 42 58" src="https://github.com/user-attachments/assets/3abc5f42-bcda-451c-a094-2802c1eb357a" />

JaCoCo를 활용해 코드 커버리지를 측정하고, JUnit 5와 Mockito를 사용하여 단위 테스트를 수행했습니다. Gradle의 jacocoTestReport 태스크를 활용해 테스트 실행 후 자동으로 커버리지 리포트를 생성해 커버리지 결과를 시각화할 수 있도록 구성했습니다.

---

## 보안
1. ORM 기반 SQL 인젝션 방어
2. OAuth2.0 + Spring Security를 통한 인증 구현
3. 쿠키 보안 설정 - XSS 방어, https 연결에서만 전송




