package com.practice.jpa.entity

import jakarta.persistence.*

/**
 * 주문 상품 엔티티
 *
 * OrderItem : Order = N : 1
 */
@Entity
class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var productName: String,

    var price: Long = 0,

    var quantity: Int = 1,

    /**
     * 주문 (연관관계 주인)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: Order? = null
) {
    /**
     * 주문 상품 금액 계산
     */
    fun getTotalPrice(): Long {
        return price * quantity
    }

    override fun toString(): String {
        return "OrderItem(id=$id, productName='$productName', price=$price, quantity=$quantity)"
    }
}
