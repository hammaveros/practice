package com.practice.jpa.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.StructuredTaskScope
import java.util.concurrent.TimeUnit
import kotlin.time.measureTime

/**
 * Java 21 가상 스레드(Virtual Thread) 예제
 *
 * 가상 스레드란?
 * - JVM이 관리하는 경량 스레드
 * - OS 스레드(플랫폼 스레드)보다 훨씬 가벼움
 * - 수백만 개 생성 가능 (플랫폼 스레드는 수천 개가 한계)
 *
 * 언제 쓰나?
 * - I/O 바운드 작업 (DB 조회, HTTP 호출, 파일 읽기)
 * - 대량의 동시 요청 처리
 *
 * 언제 안 쓰나?
 * - CPU 바운드 작업 (계산 집약적)
 * - synchronized 블록이 많은 경우 (pinning 문제)
 */
@Service
class VirtualThreadService {

    private val log = LoggerFactory.getLogger(javaClass)

    // ===========================================
    // 1. 기본 가상 스레드 생성
    // ===========================================
    /**
     * Thread.ofVirtual()로 가상 스레드 생성
     */
    fun basicVirtualThread() {
        log.info("=== 기본 가상 스레드 예제 ===")

        // 가상 스레드 생성 및 시작
        val thread = Thread.ofVirtual()
            .name("my-virtual-thread")
            .start {
                log.info("가상 스레드에서 실행 중...")
                log.info("스레드 이름: ${Thread.currentThread().name}")
                log.info("가상 스레드 여부: ${Thread.currentThread().isVirtual}")
                Thread.sleep(100)
                log.info("가상 스레드 작업 완료")
            }

        thread.join()  // 완료 대기
    }

    // ===========================================
    // 2. 가상 스레드 vs 플랫폼 스레드 성능 비교
    // ===========================================
    /**
     * I/O 작업 시뮬레이션으로 성능 비교
     *
     * 가상 스레드: I/O 대기 중 다른 작업 수행 가능
     * 플랫폼 스레드: I/O 대기 중 블로킹
     */
    fun comparePerformance(taskCount: Int = 10000) {
        log.info("=== 성능 비교: $taskCount 개 작업 ===")

        // I/O 작업 시뮬레이션 (100ms 대기)
        val ioTask = Runnable {
            Thread.sleep(100)
        }

        // 플랫폼 스레드 (고정 풀)
        val platformTime = measureTime {
            Executors.newFixedThreadPool(100).use { executor ->
                repeat(taskCount) {
                    executor.submit(ioTask)
                }
                executor.shutdown()
                executor.awaitTermination(5, TimeUnit.MINUTES)
            }
        }

        // 가상 스레드
        val virtualTime = measureTime {
            Executors.newVirtualThreadPerTaskExecutor().use { executor ->
                repeat(taskCount) {
                    executor.submit(ioTask)
                }
                executor.shutdown()
                executor.awaitTermination(5, TimeUnit.MINUTES)
            }
        }

        log.info("플랫폼 스레드 (100개 풀): ${platformTime.inWholeMilliseconds}ms")
        log.info("가상 스레드: ${virtualTime.inWholeMilliseconds}ms")
        log.info("가상 스레드가 ${platformTime.inWholeMilliseconds / virtualTime.inWholeMilliseconds}배 빠름")
    }

    // ===========================================
    // 3. ExecutorService로 가상 스레드 풀 사용
    // ===========================================
    /**
     * newVirtualThreadPerTaskExecutor()
     * - 작업마다 새 가상 스레드 생성
     * - 풀 크기 제한 없음 (가상 스레드는 가벼우니까)
     */
    fun virtualThreadExecutor() {
        log.info("=== Virtual Thread Executor 예제 ===")

        Executors.newVirtualThreadPerTaskExecutor().use { executor ->
            val futures = (1..5).map { i ->
                executor.submit<String> {
                    log.info("작업 $i 시작 - ${Thread.currentThread().name}")
                    Thread.sleep(100)
                    "작업 $i 결과"
                }
            }

            // 결과 수집
            futures.forEach { future ->
                log.info("결과: ${future.get()}")
            }
        }
    }

    // ===========================================
    // 4. Structured Concurrency (구조적 동시성)
    // ===========================================
    /**
     * Java 21 Preview 기능 (--enable-preview 필요)
     *
     * 여러 비동기 작업을 구조화된 방식으로 관리
     * - 부모 스레드가 자식 스레드의 생명주기 관리
     * - 하나라도 실패하면 나머지 취소 가능
     *
     * 주의: Preview 기능이라 프로덕션에서는 아직 비권장
     */
    fun structuredConcurrencyExample() {
        log.info("=== Structured Concurrency 예제 ===")

        // StructuredTaskScope는 Preview라서
        // 간단한 예제로 대체

        // 여러 작업을 병렬로 실행하고 모두 완료 대기
        Executors.newVirtualThreadPerTaskExecutor().use { executor ->
            val userFuture = executor.submit<String> {
                Thread.sleep(100)
                "User 정보"
            }
            val orderFuture = executor.submit<String> {
                Thread.sleep(150)
                "Order 정보"
            }
            val paymentFuture = executor.submit<String> {
                Thread.sleep(80)
                "Payment 정보"
            }

            // 모든 결과 조합
            val result = """
                사용자: ${userFuture.get()}
                주문: ${orderFuture.get()}
                결제: ${paymentFuture.get()}
            """.trimIndent()

            log.info("조합된 결과:\n$result")
        }
    }

    // ===========================================
    // 5. Spring과 가상 스레드 통합
    // ===========================================
    /**
     * Spring Boot 3.2+에서 가상 스레드 활성화
     *
     * application.yml에 추가:
     * spring:
     *   threads:
     *     virtual:
     *       enabled: true
     *
     * 이렇게 하면:
     * - 톰캣 요청 처리가 가상 스레드로
     * - @Async 작업도 가상 스레드로
     * - 별도 스레드 풀 설정 불필요
     */
    fun springVirtualThreadInfo(): String {
        val currentThread = Thread.currentThread()
        return """
            현재 스레드: ${currentThread.name}
            가상 스레드 여부: ${currentThread.isVirtual}

            Spring에서 가상 스레드 활성화:
            spring.threads.virtual.enabled=true

            효과:
            - 톰캣 요청을 가상 스레드로 처리
            - 동시 연결 수 크게 증가 가능
            - 스레드 풀 튜닝 불필요
        """.trimIndent()
    }
}
