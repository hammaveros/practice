package com.practice.virtualthread.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

/**
 * Virtual Thread 예제 서비스
 *
 * Java 21의 가상 스레드는 I/O 바운드 작업에 최적화되어 있음
 * - 네트워크 요청, DB 조회, 파일 I/O 등
 * - CPU 바운드 작업에는 효과 없음
 */
@Service
class VirtualThreadExampleService {

    private val log = LoggerFactory.getLogger(javaClass)

    // ===========================================
    // 기본 예제
    // ===========================================

    /**
     * 가상 스레드 생성하는 3가지 방법
     */
    fun basicExample() {
        log.info("=== 가상 스레드 기본 예제 ===")

        // 방법 1: Thread.startVirtualThread (가장 간단)
        val vt1 = Thread.startVirtualThread {
            log.info("[방법1] startVirtualThread - isVirtual: ${Thread.currentThread().isVirtual}")
        }

        // 방법 2: Thread.ofVirtual().start()
        val vt2 = Thread.ofVirtual()
            .name("my-virtual-thread")
            .start {
                log.info("[방법2] ofVirtual - name: ${Thread.currentThread().name}")
            }

        // 방법 3: ExecutorService (실무에서 많이 사용)
        Executors.newVirtualThreadPerTaskExecutor().use { executor ->
            executor.submit {
                log.info("[방법3] VirtualThreadPerTaskExecutor")
            }.get()
        }

        // 모든 스레드 완료 대기
        vt1.join()
        vt2.join()

        log.info("=== 기본 예제 완료 ===")
    }

    // ===========================================
    // 성능 비교
    // ===========================================

    /**
     * 플랫폼 스레드 vs 가상 스레드 성능 비교
     *
     * I/O 바운드 작업(Thread.sleep)에서 성능 차이가 극명함
     * - 플랫폼 스레드: 스레드 풀 크기에 제한됨
     * - 가상 스레드: 동시에 수만 개 작업 처리 가능
     */
    fun comparePerformance(taskCount: Int): Map<String, Any> {
        log.info("=== 성능 비교 시작 (작업 수: $taskCount) ===")

        // I/O 시뮬레이션 (100ms 대기)
        val ioTask = Runnable { Thread.sleep(100) }

        // 플랫폼 스레드 (100개 풀)
        val platformTime = measureTimeMillis {
            Executors.newFixedThreadPool(100).use { executor ->
                val futures = (1..taskCount).map { executor.submit(ioTask) }
                futures.forEach { it.get() }
            }
        }
        log.info("플랫폼 스레드 (100개 풀): ${platformTime}ms")

        // 가상 스레드
        val virtualTime = measureTimeMillis {
            Executors.newVirtualThreadPerTaskExecutor().use { executor ->
                val futures = (1..taskCount).map { executor.submit(ioTask) }
                futures.forEach { it.get() }
            }
        }
        log.info("가상 스레드: ${virtualTime}ms")

        val speedup = platformTime.toDouble() / virtualTime
        log.info("가상 스레드가 ${String.format("%.1f", speedup)}배 빠름")

        return mapOf(
            "taskCount" to taskCount,
            "platformThreadMs" to platformTime,
            "virtualThreadMs" to virtualTime,
            "speedup" to String.format("%.1fx", speedup),
            "note" to "I/O 바운드 작업에서 가상 스레드가 훨씬 효율적"
        )
    }

    // ===========================================
    // 대량 동시성
    // ===========================================

    /**
     * 대량의 동시 작업 처리
     *
     * 플랫폼 스레드는 수천 개가 한계지만
     * 가상 스레드는 수십만 개도 가능
     */
    fun massiveConcurrency(count: Int): Map<String, Any> {
        log.info("=== 대량 동시 작업: $count 개 ===")

        val completed = AtomicInteger(0)
        val startTime = System.currentTimeMillis()

        Executors.newVirtualThreadPerTaskExecutor().use { executor ->
            val futures = (1..count).map {
                executor.submit {
                    // I/O 시뮬레이션
                    Thread.sleep(50)
                    completed.incrementAndGet()
                }
            }
            futures.forEach { it.get() }
        }

        val elapsed = System.currentTimeMillis() - startTime
        log.info("완료: ${completed.get()}개 / ${elapsed}ms")

        return mapOf(
            "totalTasks" to count,
            "completed" to completed.get(),
            "elapsedMs" to elapsed,
            "throughput" to "${count * 1000 / elapsed} tasks/sec"
        )
    }

    // ===========================================
    // Structured Concurrency
    // ===========================================

    /**
     * 구조화된 동시성 예제
     *
     * 여러 작업을 논리적으로 묶어서 관리
     * - 모든 하위 작업이 완료되어야 상위 작업 완료
     * - 하나가 실패하면 나머지도 취소 가능
     *
     * 참고: StructuredTaskScope는 Preview 기능이라
     * 여기서는 비슷한 패턴을 직접 구현
     */
    fun structuredConcurrencyExample(): Map<String, Any> {
        log.info("=== Structured Concurrency 예제 ===")

        val results = mutableMapOf<String, Any>()

        Executors.newVirtualThreadPerTaskExecutor().use { executor ->
            // 병렬로 3개 작업 실행
            val userFuture = executor.submit<String> {
                Thread.sleep(100)
                "사용자 정보 조회 완료"
            }
            val orderFuture = executor.submit<String> {
                Thread.sleep(150)
                "주문 내역 조회 완료"
            }
            val recommendFuture = executor.submit<String> {
                Thread.sleep(80)
                "추천 상품 조회 완료"
            }

            // 모든 작업 완료 대기 (구조화된 방식)
            results["user"] = userFuture.get()
            results["order"] = orderFuture.get()
            results["recommend"] = recommendFuture.get()
        }

        results["message"] = "3개 작업이 병렬로 실행되어 약 150ms에 완료 (순차면 330ms)"
        return results
    }
}
