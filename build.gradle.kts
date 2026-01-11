import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Spring Boot 3.4.4 - 루트에서는 apply false로 선언만 해둠
    // 실제 적용은 각 모듈(producer, consumer)에서 함
    id("org.springframework.boot") version "3.4.4" apply false

    // Spring 의존성 버전 관리 플러그인
    // BOM(Bill of Materials)으로 Spring 관련 라이브러리 버전 자동 맞춤
    id("io.spring.dependency-management") version "1.1.7" apply false

    // Kotlin JVM 플러그인 - 코틀린 컴파일용
    kotlin("jvm") version "1.9.25"

    // Kotlin Spring 플러그인 - @Component, @Service 등에 자동으로 open 붙여줌
    // (코틀린은 기본이 final이라 Spring AOP가 안 먹힘)
    kotlin("plugin.spring") version "1.9.25" apply false

    // Kotlin JPA 플러그인 - @Entity 등에 no-arg 생성자 자동 생성
    kotlin("plugin.jpa") version "1.9.25" apply false
}

// ===========================================
// 모든 프로젝트(루트 + 서브모듈)에 공통 적용
// ===========================================
allprojects {
    group = "com.practice"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

// ===========================================
// 서브모듈(common, producer, consumer)에만 적용
// ===========================================
subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    // JDK 21 사용 설정
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    dependencies {
        // 코틀린 리플렉션 - Spring이 내부적으로 사용
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib")

        // 테스트 의존성
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            // JSR-305 null 안전성 어노테이션을 엄격하게 체크
            // Spring의 @Nullable, @NonNull 등을 코틀린에서 제대로 인식
            freeCompilerArgs.add("-Xjsr305=strict")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
