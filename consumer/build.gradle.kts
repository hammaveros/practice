// ===========================================
// consumer 모듈
// - 역할: Kafka에서 메시지를 구독하는 Spring Boot 앱
// - 실행 가능한 애플리케이션 (bootJar 생성)
// ===========================================

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

dependencies {
    // common 모듈 의존성 - DTO, 토픽명 등 공유
    implementation(project(":common"))

    // Spring Boot 기본
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Kafka - 메시지 구독용
    implementation("org.springframework.kafka:spring-kafka")

    // Jackson Kotlin 모듈 - data class JSON 변환
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}
