package com.practice.producer.service

import com.practice.common.KafkaTopics
import com.practice.common.event.OrderCreatedEventProto.OrderCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

/**
 * 주문 이벤트 발행 서비스 (Protobuf)
 *
 * Protobuf 메시지를 Schema Registry를 통해 Kafka로 발행
 * - 바이너리 직렬화로 JSON 대비 50-80% 크기 감소
 * - Schema Registry에 스키마 자동 등록/검증
 */
@Service
class OrderProducerService(
    private val kafkaTemplate: KafkaTemplate<String, OrderCreatedEvent>
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 주문 생성 이벤트 발행 (비동기)
     *
     * @param event 발행할 Protobuf 이벤트
     * @return CompletableFuture - 결과를 비동기로 받을 수 있음
     */
    fun sendOrderCreatedEvent(event: OrderCreatedEvent): CompletableFuture<SendResult<String, OrderCreatedEvent>> {
        log.info("주문 이벤트 발행 시작: orderId={}", event.orderId)

        // send()는 비동기, 바로 리턴됨
        // key로 orderId 사용 → 같은 주문은 같은 파티션으로
        return kafkaTemplate.send(
            KafkaTopics.ORDER_CREATED,  // 토픽명
            event.orderId,               // 키 (파티션 결정에 사용)
            event                        // Protobuf 메시지
        ).whenComplete { result, ex ->
            if (ex == null) {
                // 성공
                val metadata = result.recordMetadata
                log.info(
                    "주문 이벤트 발행 성공: topic={}, partition={}, offset={}",
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset()
                )
            } else {
                // 실패
                log.error("주문 이벤트 발행 실패: orderId={}", event.orderId, ex)
            }
        }
    }

    /**
     * 동기 방식 발행 (결과 기다림)
     *
     * 주의: 블로킹되므로 처리량 낮음
     * 확실히 성공 확인이 필요할 때만 사용
     */
    fun sendOrderCreatedEventSync(event: OrderCreatedEvent): SendResult<String, OrderCreatedEvent> {
        log.info("주문 이벤트 동기 발행: orderId={}", event.orderId)

        return kafkaTemplate.send(
            KafkaTopics.ORDER_CREATED,
            event.orderId,
            event
        ).get()  // get() 호출하면 결과 올 때까지 블로킹
    }
}