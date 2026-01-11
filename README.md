# Practice 멀티모듈 프로젝트

코틀린 + 스프링 학습용 멀티모듈 프로젝트

## 모듈 구조

| 모듈 | 포트 | 설명 |
|------|------|------|
| `common` | - | 공통 모듈 (이벤트 DTO 등) |
| `producer` | 8080 | Kafka Producer (주문 API) |
| `consumer` | 8081 | Kafka Consumer (주문 처리) |
| `jpa-test` | 8082 | JPA 연관관계/N+1 학습용 |
| `virtual-thread` | - | Java 21 가상스레드 학습용 |
| `kotlin-basics` | - | 코틀린 기초 문법 예제 |

## 실행 방법

### 1. 전체 빌드
```bash
gradle build
```

### 2. 각 모듈 실행

#### kotlin-basics (순수 코틀린)
```bash
gradle :kotlin-basics:run
```

#### jpa-test (스프링 + JPA)
```bash
gradle :jpa-test:bootRun
# http://localhost:8082
```

#### Kafka 프로젝트 (producer + consumer)

1. 먼저 Kafka 실행
```bash
docker-compose up -d
```

2. Producer 실행
```bash
gradle :producer:bootRun
# http://localhost:8080
```

3. Consumer 실행 (다른 터미널)
```bash
gradle :consumer:bootRun
# http://localhost:8081
```

4. 주문 API 테스트
```bash
# 단건 주문
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": "user1", "productName": "MacBook", "quantity": 1, "totalPrice": 2500000}'

# 대량 주문 (100건)
curl -X POST "http://localhost:8080/api/orders/bulk?count=100"
```

#### virtual-thread
```bash
gradle :virtual-thread:bootRun
```

### 3. Kafka 종료
```bash
docker-compose down
```

## 문서

- [01. Gradle 멀티모듈 개념과 구조](docs/01-gradle-멀티모듈-프로젝트-개념과-구조.md)
- [02. Kafka 연습 프로젝트 세팅](docs/02-kafka-연습-프로젝트-세팅-가이드.md)
- [03. JPA 연관관계/N+1 해결](docs/03-jpa-연관관계-n1-해결-가이드.md)
- [04. Java 21 가상스레드](docs/04-java21-가상스레드-가이드.md)

## 기술 스택

- Kotlin 1.9.25
- Spring Boot 3.4.4
- Java 21 (Amazon Corretto)
- Kafka
- H2 Database (인메모리)
- Gradle 8.x
