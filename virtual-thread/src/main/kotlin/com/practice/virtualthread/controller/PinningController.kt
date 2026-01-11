package com.practice.virtualthread.controller

import com.practice.virtualthread.service.PinningExampleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Pinning 및 주의사항 예제 컨트롤러
 */
@RestController
@RequestMapping("/api/caution")
class PinningController(
    private val pinningService: PinningExampleService
) {
    /**
     * Pinning 문제 시연
     * synchronized vs ReentrantLock 비교
     */
    @GetMapping("/pinning")
    fun pinning(
        @RequestParam(defaultValue = "50") taskCount: Int
    ): Map<String, Any> {
        return pinningService.pinningProblem(taskCount)
    }

    /**
     * CPU vs I/O 바운드 비교
     * 가상 스레드가 효과 있는 경우/없는 경우
     */
    @GetMapping("/cpu-vs-io")
    fun cpuVsIo(): Map<String, Any> {
        return pinningService.cpuVsIoBound()
    }
}
