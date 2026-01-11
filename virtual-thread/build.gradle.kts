// ===========================================
// virtual-thread 모듈
// - 역할: Java 21 Virtual Thread 학습용
// - Spring Boot 3.2+ 가상 스레드 지원
// ===========================================

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

dependencies {
    // ===========================================
    // Spring Web
    // - 가상 스레드로 요청 처리 테스트
    // ===========================================
    implementation("org.springframework.boot:spring-boot-starter-web")

    // ===========================================
    // 테스트
    // ===========================================
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
