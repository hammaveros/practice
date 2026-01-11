package com.practice.jpa.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 주문 엔티티
 *
 * Order : OrderItem = 1 : N
 * - 하나의 주문에 여러 주문 상품
 *
 * 실무 예제로 Team-Member보다 복잡한 케이스
 */
@Entity
@Table(name = "orders")  // order는 SQL 예약어라서 orders로
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var orderName: String,

    var orderDate: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.ORDERED,

    /**
     * 주문 상품 목록
     *
     * @BatchSize 개별 설정 (전역 설정 오버라이드)
     * - 이 컬렉션만 특별히 다른 배치 사이즈 적용 가능
     * - 여기서는 전역 설정(100) 사용하므로 주석 처리
     */
    // @BatchSize(size = 50)
    @OneToMany(
        mappedBy = "order",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val orderItems: MutableList<OrderItem> = mutableListOf()
) {
    /**
     * 주문 상품 추가
     */
    fun addOrderItem(orderItem: OrderItem) {
        orderItems.add(orderItem)
        orderItem.order = this
    }

    /**
     * 총 주문 금액 계산
     */
    fun getTotalPrice(): Long {
        return orderItems.sumOf { it.getTotalPrice() }
    }

    /**
     * 주문 취소
     */
    fun cancel() {
        status = OrderStatus.CANCELLED
    }

    override fun toString(): String {
        return "Order(id=$id, orderName='$orderName', status=$status, itemCount=${orderItems.size})"
    }
}

enum class OrderStatus {
    ORDERED,    // 주문됨
    SHIPPED,    // 배송 중
    DELIVERED,  // 배송 완료
    CANCELLED   // 취소됨
}
