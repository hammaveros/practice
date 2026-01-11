package com.practice.common

/**
 * Kafka 토픽 이름 상수
 *
 * 토픽명을 상수로 관리하면:
 * 1. 오타 방지
 * 2. IDE 자동완성 지원
 * 3. 한 곳에서 변경하면 전체 적용
 */
object KafkaTopics {
    // 주문 관련 토픽
    const val ORDER_CREATED = "order-created"
    const val ORDER_COMPLETED = "order-completed"

    // 사용자 관련 토픽
    const val USER_REGISTERED = "user-registered"

    // 테스트용 토픽
    const val TEST_TOPIC = "test-topic"
}
