package com.practice.producer.controller

import com.practice.common.event.OrderCreatedEventProto.OrderCreatedEvent
import com.practice.producer.service.OrderProducerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * 주문 API 컨트롤러
 *
 * REST API 요청을 받아 Protobuf 메시지로 변환 후 Kafka 발행
 */
@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderProducerService: OrderProducerService
) {
    /**
     * 주문 생성 API
     *
     * POST /api/orders
     * Body: { "userId": "user-1", "productName": "맥북", "quantity": 1, "totalPrice": 2500000 }
     */
    @PostMapping
    fun createOrder(@RequestBody request: CreateOrderRequest): ResponseEntity<OrderResponse> {
        // 주문 ID 생성 (실제로는 DB 저장 후 받아옴)
        val orderId = UUID.randomUUID().toString()

        // Protobuf 이벤트 생성 (Builder 패턴 사용)
        val event = OrderCreatedEvent.newBuilder()
            .setOrderId(orderId)
            .setUserId(request.userId)
            .setProductName(request.productName)
            .setQuantity(request.quantity)
            .setTotalPrice(request.totalPrice)
            .setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build()

        // Kafka로 발행 (비동기)
        orderProducerService.sendOrderCreatedEvent(event)

        // 즉시 응답 (Kafka 발행 완료 안 기다림)
        return ResponseEntity.ok(
            OrderResponse(
                orderId = orderId,
                message = "주문이 생성되었습니다. 처리 중..."
            )
        )
    }

    /**
     * 테스트용: 대량 주문 생성
     *
     * POST /api/orders/bulk?count=100
     */
    @PostMapping("/bulk")
    fun createBulkOrders(@RequestParam count: Int): ResponseEntity<BulkOrderResponse> {
        val orderIds = mutableListOf<String>()

        repeat(count) { i ->
            val orderId = UUID.randomUUID().toString()
            orderIds.add(orderId)

            // Protobuf 이벤트 생성
            val event = OrderCreatedEvent.newBuilder()
                .setOrderId(orderId)
                .setUserId("user-${i % 10}")  // 10명의 사용자가 돌아가며 주문
                .setProductName("상품-$i")
                .setQuantity((1..5).random())
                .setTotalPrice((10000L..100000L).random())
                .setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build()

            orderProducerService.sendOrderCreatedEvent(event)
        }

        return ResponseEntity.ok(
            BulkOrderResponse(
                totalCount = count,
                orderIds = orderIds.take(5),  // 처음 5개만 반환
                message = "${count}개 주문이 발행되었습니다."
            )
        )
    }
}

// ===========================================
// Request/Response DTO
// ===========================================

data class CreateOrderRequest(
    val userId: String,
    val productName: String,
    val quantity: Int,
    val totalPrice: Long
)

data class OrderResponse(
    val orderId: String,
    val message: String
)

data class BulkOrderResponse(
    val totalCount: Int,
    val orderIds: List<String>,
    val message: String
)