# 쿠폰 발급 시스템

## 개요

이 프로젝트는 Kotlin 기반의 Spring Boot WebFlux 어플리케이션으로, Redis를 활용해 쿠폰 발급 시스템을 구현한 예제입니다.  
주요 기능은 쿠폰 재고 관리, 사용자별 중복 발급 방지, 그리고 Redis Stream을 통한 비동기 발급 요청 처리입니다.

---

## 주요 기술 스택

- Kotlin
- Spring Boot WebFlux (Reactive Programming)
- Redis (Key-Value 저장소, Stream, Lua 스크립트)
- Reactor

---

## 시스템 아키텍처

Client (User/Admin)
├─ REST API 요청 (쿠폰 등록 / 발급 요청)
│
Spring Boot WebFlux API 서버
├─ 쿠폰 재고 조회 및 요청 수 관리 (Redis Key-Value)
├─ 쿠폰 발급 요청 Redis Stream 기록
│
Redis Server
├─ 쿠폰 재고, 발급 요청 카운터 저장
├─ 쿠폰 발급 요청 Stream (coupon-requests)
├─ 사용자 중복 발급 체크 키 저장
│
Redis Stream Consumer
├─ Stream 메시지 비동기 처리
├─ Lua 스크립트로 재고 감소 및 중복 체크 원자적 처리
├─ 발급 성공/실패 로그 기록


---

## 설치 및 실행 방법

1. Redis 설치 및 실행 (기본 설정 사용 권장)
   ```bash
   redis-server


---

## 설치 및 실행 방법

1. Redis 설치 및 실행 (기본 설정 사용 권장)
   ```bash
   redis-server
프로젝트 클론 및 빌드
git clone <repo-url>
cd coupon-issuance
./gradlew build


애플리케이션 실행

bash
복사
편집
./gradlew bootRun
API 호출 (기본 포트 8080)

API 명세
1. 쿠폰 등록
   URL: /api/coupons/register

Method: POST

Request Body:

json
복사
편집
{
"couponId": "SPRING2025",
"quantity": 1000
}
Response:

200 OK: 쿠폰 등록 완료 메시지

2. 쿠폰 발급 요청
   URL: /api/coupons/request

Method: POST

Request Body:

json
복사
편집
{
"userId": "user123",
"couponId": "SPRING2025"
}
Response:

200 OK: 발급 요청 성공

400 Bad Request: 재고 부족으로 발급 실패

내부 동작 상세
1) 쿠폰 등록
   관리자가 쿠폰 아이디와 수량을 API로 등록하면, Redis에 쿠폰 재고 키(coupon:{couponId}:stock)를 저장합니다.

2) 쿠폰 발급 요청
   사용자가 발급 요청 API를 호출하면 다음이 수행됩니다:

Redis에 coupon:{couponId}:requested 키의 값을 증가시켜 요청 수를 집계합니다.

요청 수가 현재 재고(coupon:{couponId}:stock)보다 많으면 즉시 실패 응답 반환.

그렇지 않으면, Redis Stream(coupon-requests)에 요청 정보를 저장해 비동기 처리 대기.

3) Redis Stream 소비 및 쿠폰 발급 처리
   별도의 Redis Stream Consumer가 주기적으로 요청 메시지를 읽습니다.

각 요청에 대해 Lua 스크립트를 실행해 다음 작업을 원자적으로 수행합니다:

재고가 남아있는지 확인

사용자가 이미 쿠폰을 받았는지 확인 (중복 체크)

재고 차감 및 사용자 중복 발급 키 생성

Lua 스크립트 결과에 따라 발급 성공/실패를 로그에 기록합니다.

Redis Lua 스크립트
lua
복사
편집
local stock = tonumber(redis.call('GET', KEYS[1]))
if not stock or stock <= 0 then return 0 end

if redis.call('SETNX', KEYS[2], 'issued') == 1 then
redis.call('DECR', KEYS[1])
return 1
else
return 2
end
KEYS[1]: 쿠폰 재고 키 (coupon:{couponId}:stock)

KEYS[2]: 사용자 발급 여부 체크 키 (coupon:{couponId}:user:{userId})

반환값:

0: 재고 없음 → 발급 실패

1: 발급 성공 (재고 차감 완료)

2: 이미 발급된 사용자 (중복 시도)

주요 고려사항 및 확장 포인트
Redis의 Lua 스크립트로 원자적 처리 보장 → 동시성 문제 예방

Redis Stream을 활용한 비동기 발급 처리로 API 부하 분산

요청 수 초과시 빠른 실패 처리로 불필요한 부하 감소

로그 저장소 확장 가능 (파일, DB, 모니터링 시스템 연동)

사용자 발급 정보 만료 정책 추가 가능 (ex: 쿠폰 유효 기간)

관리자 대시보드 개발 및 쿠폰 통계 추가 가능

참고자료
Spring Data Redis WebFlux 공식문서

Redis Lua Scripting

Reactive Programming with Reactor

문의
시스템 관련 질문이나 제안은 이슈 혹은 메일로 문의 부탁드립니다.

yaml
복사
편집

---

필요하면 PlantUML이나 실제 도구로 다이어그램도 추가해 드릴 수 있습니다.  
이 README 형식이면 개발팀과 운영팀 모두 쉽게 이해할 수 있을 거예요!







