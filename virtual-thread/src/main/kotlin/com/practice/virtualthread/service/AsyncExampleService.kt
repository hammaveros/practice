package com.practice.virtualthread.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

/**
 * @Async 예제 서비스
 *
 * AsyncConfig에서 설정한 가상 스레드 Executor로 실행됨
 */
@Service
class AsyncExampleService {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 비동기 메서드 (반환값 없음)
     *
     * 호출 즉시 반환되고, 실제 작업은 가상 스레드에서 실행
     */
    @Async
    fun asyncTask(taskName: String) {
        val thread = Thread.currentThread()
        log.info("[$taskName] 시작 - isVirtual: ${thread.isVirtual}, thread: ${thread.name}")
        Thread.sleep(1000)
        log.info("[$taskName] 완료")
    }

    /**
     * 비동기 메서드 (반환값 있음)
     *
     * CompletableFuture로 결과를 받을 수 있음
     */
    @Async
    fun asyncTaskWithResult(taskName: String): CompletableFuture<String> {
        val thread = Thread.currentThread()
        log.info("[$taskName] 시작 - isVirtual: ${thread.isVirtual}")
        Thread.sleep(500)
        return CompletableFuture.completedFuture("$taskName 결과")
    }
}
