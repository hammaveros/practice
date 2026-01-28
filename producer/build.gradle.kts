// ===========================================
// producer 모듈
// - 역할: Kafka에 Protobuf 메시지를 발행하는 Spring Boot 앱
// - Schema Registry를 통해 스키마 관리
// - 실행 가능한 애플리케이션 (bootJar 생성)
// ===========================================

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

dependencies {
    // common 모듈 의존성 - Protobuf 스키마, 토픽명 등 공유
    implementation(project(":common"))

    // Spring Boot 기본
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Kafka - 메시지 발행용
    implementation("org.springframework.kafka:spring-kafka")

    // Confluent Kafka Protobuf Serializer (Schema Registry 연동)
    implementation("io.confluent:kafka-protobuf-serializer:7.6.0")

    // Jackson Kotlin 모듈 - REST API JSON 변환용
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}