package com.practice.common.event

import java.time.LocalDateTime

/**
 * 주문 생성 이벤트 DTO
 *
 * Producer → Kafka → Consumer로 전달되는 메시지 구조
 * JSON으로 직렬화되어 Kafka에 저장됨
 */
data class OrderCreatedEvent(
    val orderId: String,           // 주문 ID (UUID 권장)
    val userId: String,            // 주문한 사용자 ID
    val productName: String,       // 상품명
    val quantity: Int,             // 수량
    val totalPrice: Long,          // 총 금액 (원 단위)
    val createdAt: LocalDateTime = LocalDateTime.now()  // 생성 시각
)
