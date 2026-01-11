package com.practice.producer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Producer 애플리케이션 진입점
 *
 * @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
 * - @Configuration: 이 클래스가 Bean 설정 클래스임
 * - @EnableAutoConfiguration: spring.factories 기반 자동 설정
 * - @ComponentScan: 하위 패키지의 @Component, @Service 등 스캔
 */
@SpringBootApplication
class ProducerApplication

fun main(args: Array<String>) {
    runApplication<ProducerApplication>(*args)
}
