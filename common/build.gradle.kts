// ===========================================
// common 모듈
// - 역할: producer/consumer가 공유하는 코드
// - DTO, 상수, 유틸 등
// - Spring Boot 앱이 아님 (bootJar X, jar O)
// ===========================================

plugins {
    // Kotlin Spring 플러그인 - data class에도 기본 생성자 만들어줌
    kotlin("plugin.spring")
}

dependencies {
    // Jackson - JSON 직렬화/역직렬화
    // Kafka 메시지를 JSON으로 주고받을 때 사용
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")
}
