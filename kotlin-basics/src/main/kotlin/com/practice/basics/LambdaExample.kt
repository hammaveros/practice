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

    // -----------------------------------------
    // 8. Map 관련 함수들
    // -----------------------------------------
    println("\n8. Map 관련 함수:")

    val map = mapOf("a" to 1, "b" to 2, "c" to 3)
    println("   원본 Map: $map")

    // keys, values, entries
    println("   keys: ${map.keys}")           // Set<String>
    println("   values: ${map.values}")       // Collection<Int>
    println("   entries: ${map.entries}")     // Set<Entry<String, Int>>

    // filterKeys: 키 조건으로 필터 -> Map 반환
    val filteredByKey = map.filterKeys { it != "a" }
    println("   filterKeys (a 제외): $filteredByKey")

    // filterValues: 값 조건으로 필터 -> Map 반환
    val filteredByValue = map.filterValues { it > 1 }
    println("   filterValues (1보다 큰): $filteredByValue")

    // mapKeys: 키 변환 -> Map 반환
    val uppercaseKeys = map.mapKeys { (key, _) -> key.uppercase() }
    println("   mapKeys (대문자): $uppercaseKeys")

    // mapValues: 값 변환 -> Map 반환
    val doubledValues = map.mapValues { (_, value) -> value * 2 }
    println("   mapValues (2배): $doubledValues")

    // getOrDefault: 없으면 기본값
    val valueOrDefault = map.getOrDefault("z", 0)
    println("   getOrDefault('z', 0): $valueOrDefault")

    // getOrElse: 없으면 람다 실행
    val valueOrElse = map.getOrElse("z") { -1 }
    println("   getOrElse('z') { -1 }: $valueOrElse")

    // -----------------------------------------
    // 9. associate 계열 (List → Map 변환)
    // -----------------------------------------
    println("\n9. associate 계열 (List → Map):")

    val fruits = listOf("apple", "banana", "cherry")
    println("   원본 List: $fruits")

    // associateWith: 원소가 키, 람다 결과가 값 -> Map<T, R>
    val withLength = fruits.associateWith { it.length }
    println("   associateWith { it.length }: $withLength")
    // {apple=5, banana=6, cherry=6}

    // associateBy: 람다 결과가 키, 원소가 값 -> Map<R, T>
    val byFirstChar = fruits.associateBy { it.first() }
    println("   associateBy { it.first() }: $byFirstChar")
    // {a=apple, b=banana, c=cherry}

    // associateBy 오버로드: 키와 값 둘 다 지정 -> Map<K, V>
    val byFirstCharWithLength = fruits.associateBy(
        keySelector = { it.first() },
        valueTransform = { it.length }
    )
    println("   associateBy (키: 첫글자, 값: 길이): $byFirstCharWithLength")
    // {a=5, b=6, c=6}

    // associate: Pair로 직접 지정 -> Map<K, V>
    val customMap = fruits.associate { it to it.uppercase() }
    println("   associate { it to it.uppercase() }: $customMap")
    // {apple=APPLE, banana=BANANA, cherry=CHERRY}

    // -----------------------------------------
    // 10. 기타 유용한 함수들
    // -----------------------------------------
    println("\n10. 기타 유용한 함수:")

    // partition: 조건으로 2개 리스트 분리 -> Pair<List, List>
    val (evensP, oddsP) = numbers.partition { it % 2 == 0 }
    println("   partition (짝/홀): 짝수=$evensP, 홀수=$oddsP")

    // chunked: n개씩 묶기 -> List<List>
    val chunked = numbers.chunked(3)
    println("   chunked(3): $chunked")

    // windowed: 슬라이딩 윈도우 -> List<List>
    val windowed = listOf(1, 2, 3, 4, 5).windowed(3)
    println("   windowed(3): $windowed")  // [[1,2,3], [2,3,4], [3,4,5]]

    // zip: 두 리스트 합치기 -> List<Pair>
    val list1 = listOf("a", "b", "c")
    val list2 = listOf(1, 2, 3)
    val zipped = list1.zip(list2)
    println("   zip: $zipped")  // [(a, 1), (b, 2), (c, 3)]

    // zipWithNext: 인접 요소끼리 -> List<Pair>
    val zipNext = listOf(1, 2, 3, 4).zipWithNext()
    println("   zipWithNext: $zipNext")  // [(1,2), (2,3), (3,4)]

    // flatten: 중첩 리스트 평탄화 -> List
    val nested = listOf(listOf(1, 2), listOf(3, 4), listOf(5))
    val flattened = nested.flatten()
    println("   flatten: $flattened")  // [1, 2, 3, 4, 5]

    // flatMap: map + flatten
    val flatMapped = fruits.flatMap { it.toList() }
    println("   flatMap (글자 분해): $flatMapped")

    // distinct: 중복 제거 -> List
    val withDupes = listOf(1, 2, 2, 3, 3, 3)
    println("   distinct: ${withDupes.distinct()}")

    // distinctBy: 특정 기준으로 중복 제거
    val distinctByAge = people.distinctBy { it.age }
    println("   distinctBy (나이): ${distinctByAge.map { it.name }}")

    // take, drop
    println("   take(3): ${numbers.take(3)}")      // 앞에서 3개
    println("   drop(3): ${numbers.drop(3)}")      // 앞에서 3개 제외
    println("   takeLast(3): ${numbers.takeLast(3)}")  // 뒤에서 3개

    // takeWhile, dropWhile
    println("   takeWhile { it < 5 }: ${numbers.takeWhile { it < 5 }}")
    println("   dropWhile { it < 5 }: ${numbers.dropWhile { it < 5 }}")

    // -----------------------------------------
    // 11. 스코프 함수 반환값 정리
    // -----------------------------------------
    println("\n11. 스코프 함수 반환값 정리:")
    println("""
    | 함수  | 접근 방식 | 반환값        | 용도              |
    |-------|----------|---------------|-------------------|
    | let   | it       | 람다 결과     | null 체크, 변환    |
    | run   | this     | 람다 결과     | 객체 설정 + 결과   |
    | with  | this     | 람다 결과     | run과 동일 (비확장)|
    | apply | this     | 자기 자신     | 객체 초기화/설정   |
    | also  | it       | 자기 자신     | 부수효과 (로깅 등) |
    """.trimIndent())

    // 실제 예제로 비교
    val testList = mutableListOf(1, 2, 3)

    val letResult = testList.let { it.sum() }       // 6 (Int)
    val runResult = testList.run { sum() }          // 6 (Int)
    val withResult = with(testList) { sum() }       // 6 (Int)
    val applyResult = testList.apply { add(4) }     // [1,2,3,4] (List)
    val alsoResult = testList.also { println("   also 내부: $it") }  // [1,2,3,4] (List)

    println("   let 결과: $letResult (타입: ${letResult::class.simpleName})")
    println("   run 결과: $runResult (타입: ${runResult::class.simpleName})")
    println("   with 결과: $withResult (타입: ${withResult::class.simpleName})")
    println("   apply 결과: $applyResult (타입: ${applyResult::class.simpleName})")
    println("   also 결과: $alsoResult (타입: ${alsoResult::class.simpleName})")
}
