package com.practice.jpa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * JPA 테스트 애플리케이션
 *
 * 학습 목표:
 * 1. 연관관계 매핑 (OneToMany, ManyToOne)
 * 2. N+1 문제 이해와 해결
 * 3. Fetch Join, Entity Graph, Batch Size 비교
 */
@SpringBootApplication
class JpaTestApplication

fun main(args: Array<String>) {
    runApplication<JpaTestApplication>(*args)
}
