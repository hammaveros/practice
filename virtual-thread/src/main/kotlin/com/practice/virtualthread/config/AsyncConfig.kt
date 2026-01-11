package com.practice.virtualthread.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * 비동기 처리 설정
 *
 * @Async 메서드가 가상 스레드에서 실행되도록 설정
 * spring.threads.virtual.enabled=true 설정만으로도 되지만
 * 명시적으로 설정하는 예제
 */
@Configuration
@EnableAsync
class AsyncConfig {

    /**
     * @Async 메서드용 Executor
     *
     * newVirtualThreadPerTaskExecutor()는
     * 작업마다 새 가상 스레드를 생성함 (가상 스레드라 괜찮음)
     */
    @Bean
    fun asyncExecutor(): Executor {
        return Executors.newVirtualThreadPerTaskExecutor()
    }
}
