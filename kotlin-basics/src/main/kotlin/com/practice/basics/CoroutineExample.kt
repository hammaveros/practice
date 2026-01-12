package com.practice.basics

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

// ===========================================
// Coroutine (코루틴) 예제
// - 면접 필수 질문: suspend, async, await
// - 비동기 프로그래밍의 핵심
// ===========================================

fun main() = runBlocking {
    println("========== Coroutine 예제 ==========\n")

    // -----------------------------------------
    // 1. 기본 개념: suspend 함수
    // -----------------------------------------
    println("1. suspend 함수:")
    println("""
    Q: suspend가 뭐야?
    A: "일시 중단 가능한 함수"
       - 실행 중 잠시 멈췄다가 나중에 재개 가능
       - 스레드를 블로킹하지 않고 대기
       - suspend 함수는 코루틴 안에서만 호출 가능
    """.trimIndent())

    val result = fetchData()
    println("   결과: $result\n")

    // -----------------------------------------
    // 2. launch vs async
    // -----------------------------------------
    println("2. launch vs async:")
    println("""
    | 구분    | 반환값       | 용도                |
    |---------|-------------|---------------------|
    | launch  | Job         | 결과 필요 없을 때    |
    | async   | Deferred<T> | 결과가 필요할 때     |
    """.trimIndent())

    // launch: 결과 안 기다림 (fire-and-forget)
    println("\n   [launch 예제]")
    val job = launch {
        delay(100)
        println("   launch 완료!")
    }
    job.join()  // 완료 대기 (결과값 없음)

    // async: 결과 기다림
    println("\n   [async 예제]")
    val deferred: Deferred<Int> = async {
        delay(100)
        42  // 반환값
    }
    val value = deferred.await()  // 결과값 받기
    println("   async 결과: $value")

    // -----------------------------------------
    // 3. async + await 병렬 처리
    // -----------------------------------------
    println("\n3. async로 병렬 처리:")

    // 순차 실행 (느림)
    val sequentialTime = measureTimeMillis {
        val a = fetchUserData()
        val b = fetchOrderData()
        println("   순차 결과: $a, $b")
    }
    println("   순차 실행 시간: ${sequentialTime}ms")

    // 병렬 실행 (빠름)
    val parallelTime = measureTimeMillis {
        val deferredA = async { fetchUserData() }
        val deferredB = async { fetchOrderData() }
        val a = deferredA.await()
        val b = deferredB.await()
        println("   병렬 결과: $a, $b")
    }
    println("   병렬 실행 시간: ${parallelTime}ms")
    println("   → 거의 절반으로 줄어듦!")

    // -----------------------------------------
    // 4. 구조화된 동시성 (Structured Concurrency)
    // -----------------------------------------
    println("\n4. 구조화된 동시성:")
    println("""
    Q: 구조화된 동시성이 뭐야?
    A: "부모 코루틴이 자식 코루틴의 생명주기를 관리"
       - 부모가 취소되면 자식도 자동 취소
       - 자식이 실패하면 부모도 실패
       - 메모리 누수 방지
    """.trimIndent())

    // coroutineScope: 자식들 완료 대기
    coroutineScope {
        launch { delay(100); println("   자식 1 완료") }
        launch { delay(50); println("   자식 2 완료") }
    }
    println("   부모도 완료 (자식들 다 끝난 후)")

    // -----------------------------------------
    // 5. CoroutineScope & Context
    // -----------------------------------------
    println("\n5. CoroutineScope & Dispatcher:")
    println("""
    | Dispatcher           | 용도                    |
    |---------------------|-------------------------|
    | Dispatchers.Default | CPU 집약적 작업         |
    | Dispatchers.IO      | 네트워크, 파일 I/O      |
    | Dispatchers.Main    | UI 업데이트 (Android)   |
    """.trimIndent())

    // withContext로 Dispatcher 전환
    val ioResult = withContext(Dispatchers.IO) {
        // I/O 작업 시뮬레이션
        delay(50)
        "I/O 작업 완료"
    }
    println("\n   withContext(IO) 결과: $ioResult")

    // -----------------------------------------
    // 6. 예외 처리
    // -----------------------------------------
    println("\n6. 예외 처리:")

    // supervisorScope: 자식 실패해도 다른 자식에 영향 없음
    supervisorScope {
        val job1 = launch {
            delay(50)
            println("   job1 성공")
        }
        val job2 = launch {
            delay(30)
            println("   job2 성공")
        }
        // job3이 실패해도 job1, job2는 계속 실행
    }

    // try-catch로 async 예외 처리
    supervisorScope {
        val deferredWithError = async {
            throw RuntimeException("에러 발생!")
        }
        try {
            deferredWithError.await()
        } catch (e: Exception) {
            println("   예외 잡음: ${e.message}")
        }
    }

    // -----------------------------------------
    // 7. 취소 (Cancellation)
    // -----------------------------------------
    println("\n7. 취소:")

    val longJob = launch {
        repeat(100) { i ->
            println("   작업 중... $i")
            delay(100)
        }
    }
    delay(350)  // 0.35초 후
    longJob.cancel()  // 취소
    longJob.join()    // 취소 완료 대기
    println("   작업 취소됨!")

    // -----------------------------------------
    // 8. 면접 질문 정리
    // -----------------------------------------
    println("\n8. 면접 질문 정리:")
    println("""
    Q1: suspend 함수란?
    A1: 일시 중단 가능한 함수. 스레드 블로킹 없이 대기 가능.
        코루틴 또는 다른 suspend 함수에서만 호출 가능.

    Q2: launch vs async 차이?
    A2: launch는 Job 반환 (결과 없음), async는 Deferred<T> 반환 (결과 있음)
        async는 await()로 결과를 받을 수 있음.

    Q3: runBlocking vs coroutineScope 차이?
    A3: runBlocking은 현재 스레드 블로킹, coroutineScope는 suspend만 함.
        runBlocking은 메인에서 코루틴 시작할 때, coroutineScope는 suspend 함수 내에서.

    Q4: Dispatcher가 뭐야?
    A4: 코루틴이 어느 스레드에서 실행될지 결정.
        Default(CPU), IO(네트워크/파일), Main(UI).

    Q5: 구조화된 동시성이란?
    A5: 부모-자식 코루틴 계층 구조. 부모 취소 시 자식 자동 취소.
        리소스 누수 방지.

    Q6: 코루틴 vs 스레드 차이?
    A6: 코루틴은 경량 스레드. 하나의 스레드에서 수천 개 코루틴 실행 가능.
        컨텍스트 스위칭 비용이 스레드보다 훨씬 적음.
    """.trimIndent())
}

// ===========================================
// suspend 함수들
// ===========================================

suspend fun fetchData(): String {
    delay(100)  // 네트워크 요청 시뮬레이션
    return "데이터 로드 완료"
}

suspend fun fetchUserData(): String {
    delay(200)  // 200ms 걸리는 API
    return "User"
}

suspend fun fetchOrderData(): String {
    delay(200)  // 200ms 걸리는 API
    return "Order"
}
