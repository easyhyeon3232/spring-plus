# SPRING PLUS

Spring Boot 기반 일정 관리 프로젝트입니다.  
JWT 인증, Spring Security, QueryDSL 검색, 트랜잭션 로그 분리, WebSocket 실시간 채팅 기능을 학습하고 적용한 프로젝트입니다.

## 기술 스택

- Java 17
- Spring Boot 3.3.3
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- QueryDSL
- WebSocket / STOMP / SockJS
- MySQL
- Lombok
- Gradle

## 주요 기능

### 인증 / 인가

- 회원가입
- 로그인
- JWT 발급
- JWT 기반 인증 처리
- Spring Security 기반 접근 제어
- 관리자 권한 API 보호

### 유저

- 유저 단건 조회
- 비밀번호 변경
- 관리자 권한으로 유저 권한 변경
- JWT payload에 nickname 포함

### 일정 Todo

- 일정 생성
- 일정 단건 조회
- 일정 목록 조회
- 날씨 조건 검색
- 수정일 기준 기간 검색
- QueryDSL 기반 일정 검색

### QueryDSL 일정 검색

일정 검색 API는 QueryDSL을 사용하여 구현했습니다.

검색 조건:

- 제목 부분 검색
- 생성일 범위 검색
- 담당자 닉네임 부분 검색
- 생성일 최신순 정렬
- 페이징 처리

응답 데이터:

- 일정 제목
- 담당자 수
- 댓글 수

API:

```http
GET /todos/search?title=회의&nickname=홍길동&startDate=2026-06-01&endDate=2026-06-19&page=1&size=10
```

### 댓글

- 일정 댓글 등록
- 일정 댓글 조회
- 댓글 조회 시 작성자 정보 fetch join 적용

### 담당자 Manager

- 일정 담당자 등록
- 일정 담당자 조회
- 일정 담당자 삭제
- 일정 작성자만 담당자 등록/삭제 가능
- 작성자는 본인을 담당자로 등록할 수 없음

### 트랜잭션 로그

매니저 등록 요청 시 로그를 저장합니다.

로그 저장은 매니저 등록 트랜잭션과 독립적으로 처리하기 위해 `REQUIRES_NEW` 옵션을 사용했습니다.

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
```

로그 상태:

- `REQUESTED`: 매니저 등록 요청
- `SUCCESS`: 매니저 등록 성공
- `FAILED`: 매니저 등록 실패

매니저 등록이 실패하더라도 로그는 별도 트랜잭션으로 커밋되어 남습니다.

### 익명 실시간 채팅

WebSocket, STOMP, SockJS를 사용하여 익명 실시간 채팅 기능을 구현했습니다.

채팅 기능:

- 채팅방 생성
- 채팅방 목록 조회
- 채팅방별 메시지 저장
- 채팅방별 최근 메시지 조회
- 채팅방별 실시간 메시지 송수신
- 익명 닉네임 기반 채팅

채팅 메시지는 다음 정보를 저장합니다.

- 어느 채팅방에서 보냈는지
- 누가 보냈는지
- 어떤 메시지를 보냈는지
- 언제 보냈는지

채팅방별 WebSocket 경로:

```text
SEND /app/chat/{roomId}/send
SUBSCRIBE /sub/chat/rooms/{roomId}
```

REST API:

```http
POST /chat/rooms
GET /chat/rooms
GET /chat/rooms/{roomId}/messages?page=1&size=30
```

채팅 화면:

```http
GET /chat.html
```

## API 목록

### Auth

| Method | URL | 설명 |
|---|---|---|
| POST | `/auth/signup` | 회원가입 |
| POST | `/auth/signin` | 로그인 |

### User

| Method | URL | 설명 |
|---|---|---|
| GET | `/users/{userId}` | 유저 단건 조회 |
| PUT | `/users` | 비밀번호 변경 |
| PATCH | `/admin/users/{userId}` | 유저 권한 변경 |

### Todo

| Method | URL | 설명 |
|---|---|---|
| POST | `/todos` | 일정 생성 |
| GET | `/todos` | 일정 목록 조회 |
| GET | `/todos/{todoId}` | 일정 단건 조회 |
| GET | `/todos/search` | QueryDSL 일정 검색 |

### Comment

| Method | URL | 설명 |
|---|---|---|
| POST | `/todos/{todoId}/comments` | 댓글 등록 |
| GET | `/todos/{todoId}/comments` | 댓글 조회 |

### Manager

| Method | URL | 설명 |
|---|---|---|
| POST | `/todos/{todoId}/managers` | 담당자 등록 |
| GET | `/todos/{todoId}/managers` | 담당자 조회 |
| DELETE | `/todos/{todoId}/managers/{managerId}` | 담당자 삭제 |

### Chat

| Method | URL | 설명 |
|---|---|---|
| POST | `/chat/rooms` | 채팅방 생성 |
| GET | `/chat/rooms` | 채팅방 목록 조회 |
| GET | `/chat/rooms/{roomId}/messages` | 채팅방 메시지 조회 |

### WebSocket

| Type | Destination | 설명 |
|---|---|---|
| CONNECT | `/ws` | WebSocket 연결 |
| SEND | `/app/chat/{roomId}/send` | 채팅 메시지 전송 |
| SUBSCRIBE | `/sub/chat/rooms/{roomId}` | 채팅방 메시지 구독 |

## 실행 방법

### 1. 환경 변수 설정

`application.yml`은 민감 정보를 환경 변수로 관리합니다.

```yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/plus
    username: ${USERNAME}
    password: ${PASSWORD}

jwt:
  secret:
    key: ${SECRET}
```

필요한 환경 변수:

```text
USERNAME
PASSWORD
SECRET
```

### 2. MySQL 데이터베이스 생성

```sql
CREATE DATABASE plus;
```

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

Windows 환경:

```bash
gradlew.bat bootRun
```

기본 포트:

```text
http://localhost:8080
```

## 채팅 테스트 방법

1. 서버 실행
2. 브라우저에서 접속

```http
http://localhost:8080/chat.html
```

3. 채팅방 이름을 입력하여 채팅방 생성
4. 생성된 채팅방 ID로 입장
5. 닉네임과 메시지를 입력하여 전송
6. 다른 브라우저에서 같은 채팅방 ID로 입장하면 실시간 메시지 수신 확인 가능

## 학습 포인트

### Spring Security 전환

기존 Filter와 ArgumentResolver 기반 인증 흐름을 Spring Security 기반으로 전환했습니다.

JWT 검증 후 `Authentication` 객체를 생성하고 `SecurityContextHolder`에 저장하여 인증된 사용자 정보를 관리합니다.

### QueryDSL 검색

복잡한 검색 조건을 QueryDSL로 구현했습니다.

선택 조건은 `BooleanExpression`을 사용하여 조건이 없으면 무시되도록 처리했습니다.

```java
.where(
    titleContains(title),
    managerNicknameContains(managerNickname),
    createdAtGoe(startDateTime),
    createdAtLoe(endDateTime)
)

```

### DTO Projection

일정 검색에서는 엔티티 전체가 아니라 필요한 데이터만 조회하기 위해 Projection을 사용했습니다.

```java
Projections.constructor(
    TodoSearchResponse.class,
    todo.title,
    manager.id.countDistinct(),
    comment.id.countDistinct()
)
```

### 트랜잭션 분리

매니저 등록 실패와 관계없이 로그가 저장되도록 로그 저장 로직을 별도 서비스로 분리하고 `REQUIRES_NEW`를 적용했습니다.

### WebSocket 채팅

단일 채팅방 구조에서 채팅방별 구독 구조로 변경했습니다.

```text
/sub/chatroom
```

에서

```text
/sub/chat/rooms/{roomId}
```

구조로 변경하여 채팅방별 메시지가 섞이지 않도록 처리했습니다.

## 진행 예정

### 대용량 데이터 처리

대용량 유저 데이터 검색 성능 개선 실습을 진행할 예정입니다.

예정 작업:

- 테스트 코드로 유저 데이터 100만 건 생성
- JDBC batch insert 적용
- 닉네임 정확 일치 검색 API 구현
- nickname 인덱스 적용 전/후 성능 비교
- README에 조회 속도 비교 결과 기록

성능 비교 표 예시:

| 단계 | 개선 방법 | 데이터 수 | 검색 조건 | 조회 시간 |
|---|---|---:|---|---:|
| 1 | 인덱스 없음 | 1,000,000 | nickname 정확 일치 | 측정 예정 |
| 2 | nickname 인덱스 추가 | 1,000,000 | nickname 정확 일치 | 측정 예정 |
| 3 | DTO Projection 적용 | 1,000,000 | nickname 정확 일치 | 측정 예정 |
