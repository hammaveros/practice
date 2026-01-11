// ===========================================
// kotlin-basics 모듈
// - 역할: 코틀린 기초 문법 예제
// - 스프링 없이 순수 코틀린
// - main 함수로 직접 실행 가능
// ===========================================

plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("com.practice.basics.LambdaExampleKt")
}

dependencies {
    // 코루틴 (비동기 처리 예제용)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}
