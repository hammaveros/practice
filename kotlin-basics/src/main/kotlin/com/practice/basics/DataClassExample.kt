package com.practice.basics

// ===========================================
// Data Class 예제
// - Java의 DTO/VO를 간결하게 만드는 방법
// - equals(), hashCode(), toString(), copy() 자동 생성
// ===========================================

/**
 * data class 기본 형태
 *
 * Java로 치면 이거 하나로:
 * - 생성자
 * - getter (val) / setter (var)
 * - equals(), hashCode()
 * - toString()
 * - copy()
 * 전부 자동 생성됨
 */
data class User(
    val id: Long,           // val = 불변 (getter만)
    val name: String,
    var email: String       // var = 가변 (getter + setter)
)

/**
 * 기본값이 있는 data class
 *
 * 기본값 있으면 해당 파라미터 생략 가능
 * User("홍길동") 이렇게 호출 가능
 */
data class UserWithDefault(
    val id: Long = 0,
    val name: String,
    val email: String = "",
    val active: Boolean = true
)

/**
 * 중첩 data class
 *
 * 복잡한 구조도 깔끔하게 표현 가능
 */
data class Order(
    val orderId: String,
    val user: User,                    // 다른 data class 포함
    val items: List<OrderItem>,        // 컬렉션도 가능
    val totalPrice: Long
)

data class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Long
)


fun main() {
    println("========== Data Class 예제 ==========\n")

    // -----------------------------------------
    // 1. 기본 생성
    // -----------------------------------------
    val user1 = User(
        id = 1,
        name = "홍길동",
        email = "hong@example.com"
    )
    println("1. 기본 생성:")
    println("   user1 = $user1")
    // 출력: User(id=1, name=홍길동, email=hong@example.com)
    // toString() 자동 생성됨

    // -----------------------------------------
    // 2. copy() - 일부만 변경한 복사본 생성
    // -----------------------------------------
    // 불변 객체 패턴에서 유용
    // 원본은 그대로, 일부만 바꾼 새 객체 생성
    val user2 = user1.copy(email = "hong2@example.com")
    println("\n2. copy()로 일부만 변경:")
    println("   원본: $user1")
    println("   복사본: $user2")
    println("   원본 == 복사본? ${user1 == user2}")  // false (email이 다름)

    // -----------------------------------------
    // 3. equals() 자동 생성
    // -----------------------------------------
    val user3 = User(id = 1, name = "홍길동", email = "hong@example.com")
    println("\n3. equals() 비교:")
    println("   user1 == user3? ${user1 == user3}")  // true (값이 같으면 같음)
    println("   user1 === user3? ${user1 === user3}") // false (다른 인스턴스)

    // -----------------------------------------
    // 4. 구조 분해 선언 (Destructuring)
    // -----------------------------------------
    // 프로퍼티 순서대로 분해 가능
    val (id, name, email) = user1
    println("\n4. 구조 분해:")
    println("   id=$id, name=$name, email=$email")

    // 일부만 사용할 때는 _ 로 무시
    val (_, userName, _) = user1
    println("   이름만: $userName")

    // -----------------------------------------
    // 5. 기본값 활용
    // -----------------------------------------
    // 기본값 있는 파라미터는 생략 가능
    val userWithDefault = UserWithDefault(name = "김철수")
    println("\n5. 기본값 활용:")
    println("   $userWithDefault")
    // 출력: UserWithDefault(id=0, name=김철수, email=, active=true)

    // -----------------------------------------
    // 6. 중첩 구조
    // -----------------------------------------
    val order = Order(
        orderId = "ORD-001",
        user = user1,
        items = listOf(
            OrderItem("P001", "맥북 프로", 1, 2500000),
            OrderItem("P002", "에어팟", 2, 250000)
        ),
        totalPrice = 3000000
    )
    println("\n6. 중첩 구조:")
    println("   주문: ${order.orderId}")
    println("   주문자: ${order.user.name}")
    println("   상품 수: ${order.items.size}개")
    order.items.forEach { item ->
        println("   - ${item.productName} x ${item.quantity}")
    }
}
