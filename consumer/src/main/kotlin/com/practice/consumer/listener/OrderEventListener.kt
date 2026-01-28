package com.practice.consumer.listener

import com.practice.common.KafkaTopics
import com.practice.common.event.OrderCreatedEventProto.OrderCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

/**
 * 주문 이벤트 리스너 (Protobuf)
 *
 * Kafka에서 Protobuf 메시지를 받아서 처리하는 핵심 컴포넌트
 * - Schema Registry에서 스키마 조회 후 역직렬화
 * - 수동 커밋으로 exactly-once 처리 보장
 */
@Component
class OrderEventListener {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 주문 생성 이벤트 처리
     *
     * @KafkaListener 어노테이션으로 토픽 구독
     * - topics: 구독할 토픽
     * - groupId: Consumer Group (설정 파일 값 오버라이드 가능)
     * - containerFactory: 사용할 리스너 컨테이너
     */
    @KafkaListener(
        topics = [KafkaTopics.ORDER_CREATED],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun handleOrderCreated(
        @Payload event: OrderCreatedEvent,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        acknowledgment: Acknowledgment  // 수동 커밋용
    ) {
        log.info("========== 주문 이벤트 수신 (Protobuf) ==========")
        log.info("파티션: {}, 오프셋: {}", partition, offset)
        log.info("주문 ID: {}", event.orderId)
        log.info("사용자: {}", event.userId)
        log.info("상품: {} x {}", event.productName, event.quantity)
        log.info("금액: {}원", event.totalPrice)
        log.info("생성 시각: {}", event.createdAt)
        log.info("================================================")

        try {
            // ========================================
            // 여기서 실제 비즈니스 로직 수행
            // 예: 재고 차감, 알림 발송, 외부 API 호출 등
            // ========================================

            // 처리 시뮬레이션 (실제로는 삭제)
            processOrder(event)

            // 처리 성공 → 오프셋 커밋
            acknowledgment.acknowledge()
            log.info("오프셋 커밋 완료: partition={}, offset={}", partition, offset)

        } catch (e: Exception) {
            // 처리 실패 → 커밋 안 함 → 나중에 다시 처리됨
            log.error("주문 처리 실패: orderId={}", event.orderId, e)
            // 재시도 로직, DLQ(Dead Letter Queue) 전송 등 추가 가능
            throw e
        }
    }

    /**
     * 주문 처리 로직 (예시)
     */
    private fun processOrder(event: OrderCreatedEvent) {
        // 처리 시간 시뮬레이션
        Thread.sleep(100)

        log.debug("주문 처리 완료: {}", event.orderId)
    }
}