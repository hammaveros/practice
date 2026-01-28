package com.practice.common

/**
 * Kafka 토픽 이름 상수
 *
 * 토픽명을 상수로 관리하면:
 * 1. 오타 방지
 * 2. IDE 자동완성 지원
 * 3. 한 곳에서 변경하면 전체 적용
 *
 * 명명 규칙: {환경}.{서비스명}.{도메인}.{이벤트}
 * 예: local.order-sample.order.created
 */
object KafkaTopics {
    // 주문 관련 토픽 (Protobuf 스키마 사용)
    const val ORDER_CREATED = "local.order-sample.order.created"
    const val ORDER_COMPLETED = "local.order-sample.order.completed"

    // 사용자 관련 토픽
    const val USER_REGISTERED = "local.order-sample.user.registered"

    // 테스트용 토픽
    const val TEST_TOPIC = "local.order-sample.test.test"
}