package com.practice.virtualthread.controller

import com.practice.virtualthread.service.AsyncExampleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @Async 예제 컨트롤러
 */
@RestController
@RequestMapping("/api/async")
class AsyncController(
    private val asyncService: AsyncExampleService
) {
    /**
     * 비동기 작업 여러 개 실행
     * 즉시 응답하고, 실제 작업은 백그라운드에서 실행
     */
    @GetMapping("/fire-and-forget")
    fun fireAndForget(): String {
        asyncService.asyncTask("작업1")
        asyncService.asyncTask("작업2")
        asyncService.asyncTask("작업3")
        return "3개 비동기 작업 시작됨 - 콘솔 확인"
    }

    /**
     * 비동기 작업 결과 수집
     */
    @GetMapping("/with-result")
    fun withResult(): Map<String, Any> {
        val future1 = asyncService.asyncTaskWithResult("API호출")
        val future2 = asyncService.asyncTaskWithResult("DB조회")
        val future3 = asyncService.asyncTaskWithResult("캐시조회")

        // 모든 결과 수집
        return mapOf(
            "result1" to future1.get(),
            "result2" to future2.get(),
            "result3" to future3.get(),
            "note" to "병렬 실행되어 약 500ms에 완료 (순차면 1500ms)"
        )
    }
}
