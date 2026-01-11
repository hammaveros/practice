package com.practice.jpa.repository

import com.practice.jpa.entity.Order
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * 주문 레포지토리
 */
interface OrderRepository : JpaRepository<Order, Long> {

    /**
     * Fetch Join으로 주문 + 주문상품 조회
     *
     * DISTINCT 필수!
     * - OneToMany 조인하면 데이터 뻥튀기
     * - 주문1에 상품3개면 주문1이 3줄로 나옴
     * - DISTINCT로 중복 제거
     */
    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.orderItems")
    fun findAllWithOrderItemsFetchJoin(): List<Order>

    /**
     * Entity Graph
     */
    @EntityGraph(attributePaths = ["orderItems"])
    @Query("SELECT o FROM Order o")
    fun findAllWithOrderItemsEntityGraph(): List<Order>
}
