// ===========================================
// common 모듈
// - 역할: producer/consumer가 공유하는 코드
// - Protobuf 스키마 정의 및 코드 생성
// - DTO, 상수, 유틸 등
// - Spring Boot 앱이 아님 (bootJar X, jar O)
// ===========================================

plugins {
    // Kotlin Spring 플러그인 - data class에도 기본 생성자 만들어줌
    kotlin("plugin.spring")

    // Protobuf 플러그인 - .proto 파일에서 Java/Kotlin 코드 생성
    id("com.google.protobuf") version "0.9.4"
}

// ===========================================
// Protobuf 설정
// ===========================================
protobuf {
    // protoc 컴파일러 버전 지정
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }

    // 생성된 코드 경로 설정 (기본값 사용)
    // generated-sources/protobuf/main/java 에 생성됨
}

dependencies {
    // ===========================================
    // Protobuf 관련 의존성
    // ===========================================

    // Protobuf 런타임 라이브러리
    api("com.google.protobuf:protobuf-java:3.25.3")

    // Confluent Kafka Protobuf Serializer
    // Schema Registry와 통신하며 직렬화/역직렬화 수행
    api("io.confluent:kafka-protobuf-serializer:7.6.0")

    // ===========================================
    // Jackson (기존 JSON 직렬화용 - 유지)
    // ===========================================
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")
}

// ===========================================
// 소스 디렉토리에 생성된 Protobuf 코드 포함
// ===========================================
sourceSets {
    main {
        java {
            srcDir("build/generated/source/proto/main/java")
        }
    }
}