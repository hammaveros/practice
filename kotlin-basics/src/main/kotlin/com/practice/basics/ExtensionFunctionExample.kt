package com.practice.basics

// ===========================================
// Extension Function (확장 함수) 예제
// - 기존 클래스에 함수 추가
// - 원본 클래스 수정 없이 기능 확장
// ===========================================

// -----------------------------------------
// 1. String 확장 함수
// -----------------------------------------

/**
 * 문자열이 이메일 형식인지 확인
 *
 * 사용: "test@example.com".isEmail()
 */
fun String.isEmail(): Boolean {
    return this.contains("@") && this.contains(".")
}

/**
 * 문자열을 마스킹 처리
 *
 * 사용: "홍길동".mask(1)  // "홍**"
 */
fun String.mask(visibleCount: Int = 1): String {
    if (this.length <= visibleCount) return this
    return this.take(visibleCount) + "*".repeat(this.length - visibleCount)
}

/**
 * 문자열을 특정 길이로 자르고 ... 붙이기
 *
 * 사용: "Hello World".truncate(5)  // "Hello..."
 */
fun String.truncate(maxLength: Int, suffix: String = "..."): String {
    return if (this.length <= maxLength) this
    else this.take(maxLength) + suffix
}


// -----------------------------------------
// 2. 숫자 확장 함수
// -----------------------------------------

/**
 * 숫자를 통화 형식으로 변환
 *
 * 사용: 1000000.toCurrency()  // "1,000,000원"
 */
fun Int.toCurrency(unit: String = "원"): String {
    return String.format("%,d", this) + unit
}

fun Long.toCurrency(unit: String = "원"): String {
    return String.format("%,d", this) + unit
}

/**
 * 퍼센트 계산
 *
 * 사용: 75.percentOf(100)  // 75.0
 */
fun Int.percentOf(total: Int): Double {
    if (total == 0) return 0.0
    return (this.toDouble() / total) * 100
}


// -----------------------------------------
// 3. 컬렉션 확장 함수
// -----------------------------------------

/**
 * 리스트에서 두 번째 요소 가져오기 (없으면 null)
 */
fun <T> List<T>.secondOrNull(): T? {
    return if (this.size >= 2) this[1] else null
}

/**
 * 리스트를 청크로 나누기 (이미 있지만 예제용)
 */
fun <T> List<T>.splitIntoChunks(chunkSize: Int): List<List<T>> {
    return this.chunked(chunkSize)
}

/**
 * 조건에 맞는 요소가 없으면 기본값 반환
 */
fun <T> List<T>.findOrDefault(default: T, predicate: (T) -> Boolean): T {
    return this.find(predicate) ?: default
}


// -----------------------------------------
// 4. Nullable 확장 함수
// -----------------------------------------

/**
 * null이면 기본값, 아니면 변환
 *
 * String?.mapOrDefault("기본값") { it.uppercase() }
 */
fun <T, R> T?.mapOrDefault(default: R, transform: (T) -> R): R {
    return if (this != null) transform(this) else default
}

/**
 * null이거나 비어있으면 true
 */
fun String?.isNullOrBlankCustom(): Boolean {
    return this == null || this.isBlank()
}


// -----------------------------------------
// 5. 실전 예제: 비즈니스 로직 확장
// -----------------------------------------

data class Product(
    val id: String,
    val name: String,
    val price: Long,
    val discountRate: Int = 0  // 할인율 (0~100)
)

/**
 * 할인 적용된 가격 계산
 */
fun Product.discountedPrice(): Long {
    return price - (price * discountRate / 100)
}

/**
 * 가격 표시 문자열
 */
fun Product.displayPrice(): String {
    return if (discountRate > 0) {
        "${price.toCurrency()} → ${discountedPrice().toCurrency()} ($discountRate% 할인)"
    } else {
        price.toCurrency()
    }
}

/**
 * 상품 목록에서 총 가격 계산 (할인 적용)
 */
fun List<Product>.totalDiscountedPrice(): Long {
    return this.sumOf { it.discountedPrice() }
}


fun main() {
    println("========== Extension Function 예제 ==========\n")

    // -----------------------------------------
    // String 확장 함수 테스트
    // -----------------------------------------
    println("1. String 확장 함수:")
    println("   \"test@example.com\".isEmail() = ${"test@example.com".isEmail()}")
    println("   \"not-email\".isEmail() = ${"not-email".isEmail()}")
    println("   \"홍길동\".mask(1) = ${"홍길동".mask(1)}")
    println("   \"01012345678\".mask(3) = ${"01012345678".mask(3)}")
    println("   \"Hello World\".truncate(5) = ${"Hello World".truncate(5)}")

    // -----------------------------------------
    // 숫자 확장 함수 테스트
    // -----------------------------------------
    println("\n2. 숫자 확장 함수:")
    println("   2500000.toCurrency() = ${2500000.toCurrency()}")
    println("   2500000L.toCurrency(\"달러\") = ${2500000L.toCurrency("달러")}")
    println("   75.percentOf(100) = ${75.percentOf(100)}%")

    // -----------------------------------------
    // 컬렉션 확장 함수 테스트
    // -----------------------------------------
    println("\n3. 컬렉션 확장 함수:")
    val numbers = listOf(10, 20, 30, 40, 50)
    println("   numbers = $numbers")
    println("   numbers.secondOrNull() = ${numbers.secondOrNull()}")
    println("   numbers.splitIntoChunks(2) = ${numbers.splitIntoChunks(2)}")
    println("   numbers.findOrDefault(0) { it > 100 } = ${numbers.findOrDefault(0) { it > 100 }}")

    // -----------------------------------------
    // Nullable 확장 함수 테스트
    // -----------------------------------------
    println("\n4. Nullable 확장 함수:")
    val str: String? = "hello"
    val nullStr: String? = null
    println("   str.mapOrDefault(\"기본\") { it.uppercase() } = ${str.mapOrDefault("기본") { it.uppercase() }}")
    println("   nullStr.mapOrDefault(\"기본\") { it.uppercase() } = ${nullStr.mapOrDefault("기본") { it.uppercase() }}")

    // -----------------------------------------
    // 비즈니스 로직 확장 테스트
    // -----------------------------------------
    println("\n5. 비즈니스 로직 확장:")
    val products = listOf(
        Product("P001", "맥북 프로", 2500000, 10),
        Product("P002", "아이패드", 1200000, 0),
        Product("P003", "에어팟", 250000, 20)
    )

    products.forEach { product ->
        println("   ${product.name}: ${product.displayPrice()}")
    }
    println("   총 가격: ${products.totalDiscountedPrice().toCurrency()}")
}
