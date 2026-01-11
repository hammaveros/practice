package com.practice.basics

// ===========================================
// Lambda (람다) 예제
// - 코틀린에서 가장 많이 쓰는 문법
// - 함수를 변수처럼 다룰 수 있음
// ===========================================

fun main() {
    println("========== Lambda 예제 ==========\n")

    // -----------------------------------------
    // 1. 람다 기본 문법
    // -----------------------------------------
    // { 파라미터 -> 본문 }
    val sum: (Int, Int) -> Int = { a, b -> a + b }
    println("1. 기본 람다:")
    println("   sum(3, 5) = ${sum(3, 5)}")

    // 파라미터 하나면 it 키워드 사용 가능
    val double: (Int) -> Int = { it * 2 }
    println("   double(4) = ${double(4)}")

    // -----------------------------------------
    // 2. 컬렉션 + 람다 (가장 많이 씀)
    // -----------------------------------------
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    println("\n2. 컬렉션 + 람다:")
    println("   원본: $numbers")

    // filter: 조건에 맞는 것만 필터링
    val evens = numbers.filter { it % 2 == 0 }
    println("   짝수만: $evens")

    // map: 각 요소를 변환
    val doubled = numbers.map { it * 2 }
    println("   2배: $doubled")

    // filter + map 체이닝
    val result = numbers
        .filter { it % 2 == 0 }  // 짝수만
        .map { it * 10 }          // 10배
    println("   짝수 x 10: $result")

    // -----------------------------------------
    // 3. 자주 쓰는 컬렉션 함수들
    // -----------------------------------------
    println("\n3. 자주 쓰는 컬렉션 함수:")

    // find: 조건에 맞는 첫 번째 요소 (없으면 null)
    val firstEven = numbers.find { it % 2 == 0 }
    println("   find (첫 짝수): $firstEven")

    // firstOrNull: find와 동일
    val first = numbers.firstOrNull { it > 5 }
    println("   firstOrNull (5보다 큰 첫 번째): $first")

    // any: 하나라도 조건 만족하면 true
    val hasEven = numbers.any { it % 2 == 0 }
    println("   any (짝수 있음?): $hasEven")

    // all: 모두 조건 만족하면 true
    val allPositive = numbers.all { it > 0 }
    println("   all (모두 양수?): $allPositive")

    // none: 모두 조건 불만족하면 true
    val noNegative = numbers.none { it < 0 }
    println("   none (음수 없음?): $noNegative")

    // count: 조건 만족하는 개수
    val evenCount = numbers.count { it % 2 == 0 }
    println("   count (짝수 개수): $evenCount")

    // -----------------------------------------
    // 4. 그룹핑 & 정렬
    // -----------------------------------------
    println("\n4. 그룹핑 & 정렬:")

    data class Person(val name: String, val age: Int, val city: String)

    val people = listOf(
        Person("홍길동", 25, "서울"),
        Person("김철수", 30, "부산"),
        Person("이영희", 25, "서울"),
        Person("박민수", 35, "대전")
    )

    // groupBy: 특정 기준으로 그룹핑
    val byCity = people.groupBy { it.city }
    println("   도시별 그룹:")
    byCity.forEach { (city, persons) ->
        println("     $city: ${persons.map { it.name }}")
    }

    // sortedBy: 특정 기준으로 정렬
    val sortedByAge = people.sortedBy { it.age }
    println("   나이순: ${sortedByAge.map { "${it.name}(${it.age})" }}")

    // sortedByDescending: 내림차순
    val sortedByAgeDesc = people.sortedByDescending { it.age }
    println("   나이 역순: ${sortedByAgeDesc.map { "${it.name}(${it.age})" }}")

    // -----------------------------------------
    // 5. reduce & fold
    // -----------------------------------------
    println("\n5. reduce & fold:")

    // reduce: 누적 연산 (초기값 없음, 첫 요소가 초기값)
    val sumAll = numbers.reduce { acc, num -> acc + num }
    println("   reduce (합계): $sumAll")

    // fold: 누적 연산 (초기값 있음)
    val sumWithInit = numbers.fold(100) { acc, num -> acc + num }
    println("   fold (100 + 합계): $sumWithInit")

    // -----------------------------------------
    // 6. 실전 예제: 주문 처리
    // -----------------------------------------
    println("\n6. 실전 예제:")

    data class OrderItem(val name: String, val price: Int, val quantity: Int)

    val orderItems = listOf(
        OrderItem("맥북", 2500000, 1),
        OrderItem("에어팟", 250000, 2),
        OrderItem("아이패드", 1200000, 1),
        OrderItem("케이스", 30000, 3)
    )

    // 총 금액 계산
    val totalPrice = orderItems.sumOf { it.price * it.quantity }
    println("   총 금액: ${String.format("%,d", totalPrice)}원")

    // 100만원 이상 상품만
    val expensiveItems = orderItems
        .filter { it.price >= 1000000 }
        .map { it.name }
    println("   100만원 이상: $expensiveItems")

    // 가장 비싼 상품
    val mostExpensive = orderItems.maxByOrNull { it.price }
    println("   가장 비싼 상품: ${mostExpensive?.name}")

    // -----------------------------------------
    // 7. let, run, apply, also (스코프 함수)
    // -----------------------------------------
    println("\n7. 스코프 함수:")

    // let: null 체크 + 변환에 자주 사용
    val nullableStr: String? = "Hello"
    nullableStr?.let {
        println("   let: ${it.uppercase()}")  // it으로 접근
    }

    // run: 객체 초기화 + 결과 반환
    val length = "Hello World".run {
        println("   run: 문자열 = $this")  // this로 접근
        this.length  // 마지막 줄이 반환값
    }
    println("   run 결과: $length")

    // apply: 객체 설정에 사용 (자기 자신 반환)
    data class Config(var host: String = "", var port: Int = 0)
    val config = Config().apply {
        host = "localhost"  // this 생략 가능
        port = 8080
    }
    println("   apply: $config")

    // also: 부수 효과 (로깅 등)에 사용 (자기 자신 반환)
    val processed = "hello"
        .also { println("   also: 원본 = $it") }
        .uppercase()
        .also { println("   also: 대문자 = $it") }
    println("   also 최종: $processed")
}
