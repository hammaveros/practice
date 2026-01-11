package com.practice.virtualthread.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.system.measureTimeMillis

/**
 * Pinning 문제 예제 서비스
 *
 * 가상 스레드가 synchronized 블록에서 대기하면
 * 플랫폼 스레드도 함께 블로킹됨 (Pinning)
 *
 * 해결책: synchronized 대신 ReentrantLock 사용
 */
@Service
class PinningExampleService {

    private val log = LoggerFactory.getLogger(javaClass)

    // synchronized용 락 객체
    private val syncLock = Any()

    // ReentrantLock (가상 스레드 친화적)
    private val reentrantLock = ReentrantLock()

    /**
     * Pinning 문제 시연
     *
     * synchronized 블록 안에서 I/O 대기하면
     * 가상 스레드의 장점이 사라짐
     */
    fun pinningProblem(taskCount: Int): Map<String, Any> {
        log.info("=== Pinning 문제 시연 ($taskCount 작업) ===")

        // synchronized 사용 (Pinning 발생)
        val syncTime = measureTimeMillis {
            Executors.newVirtualThreadPerTaskExecutor().use { executor ->
                val futures = (1..taskCount).map {
                    executor.submit {
                        synchronized(syncLock) {
                            Thread.sleep(100)  // I/O 시뮬레이션
                        }
                    }
                }
                futures.forEach { it.get() }
            }
        }
        log.info("synchronized 사용: ${syncTime}ms")

        // ReentrantLock 사용 (Pinning 없음)
        val lockTime = measureTimeMillis {
            Executors.newVirtualThreadPerTaskExecutor().use { executor ->
                val futures = (1..taskCount).map {
                    executor.submit {
                        reentrantLock.withLock {
                            Thread.sleep(100)  // I/O 시뮬레이션
                        }
                    }
                }
                futures.forEach { it.get() }
            }
        }
        log.info("ReentrantLock 사용: ${lockTime}ms")

        return mapOf(
            "taskCount" to taskCount,
            "synchronizedMs" to syncTime,
            "reentrantLockMs" to lockTime,
            "recommendation" to "가상 스레드에서는 synchronized 대신 ReentrantLock 사용"
        )
    }

    /**
     * CPU 바운드 vs I/O 바운드 비교
     *
     * 가상 스레드는 I/O 바운드에서만 효과 있음
     */
    fun cpuVsIoBound(): Map<String, Any> {
        log.info("=== CPU vs I/O 바운드 비교 ===")

        val taskCount = 100

        // CPU 바운드 작업
        val cpuTask = Runnable {
            var sum = 0.0
            repeat(100_000) { sum += Math.random() }
        }

        // I/O 바운드 작업
        val ioTask = Runnable {
            Thread.sleep(100)
        }

        // CPU 바운드 - 플랫폼 스레드
        val cpuPlatformTime = measureTimeMillis {
            Executors.newFixedThreadPool(10).use { executor ->
                val futures = (1..taskCount).map { executor.submit(cpuTask) }
                futures.forEach { it.get() }
            }
        }

        // CPU 바운드 - 가상 스레드
        val cpuVirtualTime = measureTimeMillis {
            Executors.newVirtualThreadPerTaskExecutor().use { executor ->
                val futures = (1..taskCount).map { executor.submit(cpuTask) }
                futures.forEach { it.get() }
            }
        }

        // I/O 바운드 - 플랫폼 스레드
        val ioPlatformTime = measureTimeMillis {
            Executors.newFixedThreadPool(10).use { executor ->
                val futures = (1..taskCount).map { executor.submit(ioTask) }
                futures.forEach { it.get() }
            }
        }

        // I/O 바운드 - 가상 스레드
        val ioVirtualTime = measureTimeMillis {
            Executors.newVirtualThreadPerTaskExecutor().use { executor ->
                val futures = (1..taskCount).map { executor.submit(ioTask) }
                futures.forEach { it.get() }
            }
        }

        return mapOf(
            "taskCount" to taskCount,
            "cpuBound" to mapOf(
                "platformMs" to cpuPlatformTime,
                "virtualMs" to cpuVirtualTime,
                "note" to "CPU 작업은 가상 스레드 효과 없음"
            ),
            "ioBound" to mapOf(
                "platformMs" to ioPlatformTime,
                "virtualMs" to ioVirtualTime,
                "note" to "I/O 작업은 가상 스레드가 ${ioPlatformTime / ioVirtualTime}배 빠름"
            )
        )
    }
}
