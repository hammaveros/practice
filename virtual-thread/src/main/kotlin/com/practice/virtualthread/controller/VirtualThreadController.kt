package com.practice.virtualthread.controller

import com.practice.virtualthread.service.VirtualThreadExampleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Virtual Thread 예제 컨트롤러
 *
 * Spring Boot 3.2+ 에서 spring.threads.virtual.enabled=true 설정 시
 * 모든 요청이 가상 스레드에서 처리됨
 */
@RestController
@RequestMapping("/api/virtual-thread")
class VirtualThreadController(
    private val exampleService: VirtualThreadExampleService
) {
    // ===========================================
    // 현재 스레드 정보
    // ===========================================

    /**
     * 현재 요청을 처리하는 스레드 정보
     * - isVirtual: true면 가상 스레드로 처리 중
     */
    @GetMapping("/info")
    fun threadInfo(): Map<String, Any> {
        val thread = Thread.currentThread()
        return mapOf(
            "threadName" to thread.name,
            "isVirtual" to thread.isVirtual,
            "threadId" to thread.threadId(),
            "message" to if (thread.isVirtual) "가상 스레드로 요청 처리 중!" else "플랫폼 스레드로 요청 처리 중"
        )
    }

    // ===========================================
    // 기본 예제
    // ===========================================

    /**
     * 가상 스레드 기본 생성/실행
     */
    @GetMapping("/basic")
    fun basicExample(): String {
        exampleService.basicExample()
        return "기본 예제 실행 완료 - 콘솔 확인"
    }

    // ===========================================
    // 성능 비교
    // ===========================================

    /**
     * 플랫폼 스레드 vs 가상 스레드 성능 비교
     * I/O 바운드 작업에서 가상 스레드가 훨씬 빠름
     */
    @GetMapping("/compare")
    fun comparePerformance(
        @RequestParam(defaultValue = "1000") taskCount: Int
    ): Map<String, Any> {
        return exampleService.comparePerformance(taskCount)
    }

    // ===========================================
    // 동시 요청 처리
    // ===========================================

    /**
     * 대량의 동시 작업 처리
     * 가상 스레드는 수십만 개도 가능
     */
    @GetMapping("/massive")
    fun massiveConcurrency(
        @RequestParam(defaultValue = "10000") count: Int
    ): Map<String, Any> {
        return exampleService.massiveConcurrency(count)
    }

    // ===========================================
    // Structured Concurrency (미리보기)
    // ===========================================

    /**
     * StructuredTaskScope 예제 (Java 21 Preview)
     * 여러 작업을 구조화된 방식으로 관리
     */
    @GetMapping("/structured")
    fun structuredConcurrency(): Map<String, Any> {
        return exampleService.structuredConcurrencyExample()
    }
}
