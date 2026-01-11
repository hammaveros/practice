// ===========================================
// jpa-test 모듈
// - 역할: JPA 연관관계, N+1 문제, 최적화 학습용
// - H2 인메모리 DB 사용
// ===========================================

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")  // JPA 엔티티용 (no-arg 생성자 자동 생성)
}

// ===========================================
// JPA 플러그인 설정
// ===========================================
allOpen {
    // JPA 엔티티는 프록시를 위해 open이어야 함
    // 이 설정으로 @Entity, @MappedSuperclass, @Embeddable 클래스를 자동으로 open
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    // ===========================================
    // Spring Data JPA
    // ===========================================
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // ===========================================
    // H2 Database (인메모리)
    // - 테스트/학습용으로 적합
    // - 애플리케이션 종료 시 데이터 사라짐
    // ===========================================
    runtimeOnly("com.h2database:h2")

    // ===========================================
    // Web (API 테스트용)
    // ===========================================
    implementation("org.springframework.boot:spring-boot-starter-web")

    // ===========================================
    // 테스트
    // ===========================================
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
