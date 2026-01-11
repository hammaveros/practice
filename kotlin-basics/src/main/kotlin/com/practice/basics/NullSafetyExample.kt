package com.practice.basics

// ===========================================
// Null Safety 예제
// - 코틀린의 핵심 기능
// - 컴파일 타임에 NPE 방지
// ===========================================

fun main() {
    println("========== Null Safety 예제 ==========\n")

    // -----------------------------------------
    // 1. Nullable vs Non-null
    // -----------------------------------------
    println("1. Nullable vs Non-null:")

    // Non-null: null 불가능 (기본)
    var nonNull: String = "Hello"
    // nonNull = null  // 컴파일 에러!

    // Nullable: ? 붙이면 null 가능
    var nullable: String? = "Hello"
    nullable = null  // OK
    println("   nullable = $nullable")

    // -----------------------------------------
    // 2. Safe Call (?.)
    // -----------------------------------------
    println("\n2. Safe Call (?.):")

    val str: String? = "Hello World"
    val nullStr: String? = null

    // ?. : null이면 전체가 null, 아니면 실행
    println("   str?.length = ${str?.length}")           // 11
    println("   nullStr?.length = ${nullStr?.length}")   // null (NPE 안남)

    // 체이닝도 가능
    data class Address(val city: String?)
    data class Company(val address: Address?)
    data class Employee(val company: Company?)

    val employee: Employee? = Employee(Company(Address("서울")))
    val city = employee?.company?.address?.city
    println("   employee?.company?.address?.city = $city")

    // -----------------------------------------
    // 3. Elvis 연산자 (?:)
    // -----------------------------------------
    println("\n3. Elvis 연산자 (?:):")

    // null이면 기본값 사용
    val length1 = str?.length ?: 0
    val length2 = nullStr?.length ?: 0
    println("   str?.length ?: 0 = $length1")      // 11
    println("   nullStr?.length ?: 0 = $length2")  // 0

    // return이나 throw와 함께 사용
    fun getLength(s: String?): Int {
        // null이면 바로 리턴
        val len = s?.length ?: return -1
        return len
    }
    println("   getLength(\"hello\") = ${getLength("hello")}")
    println("   getLength(null) = ${getLength(null)}")

    // -----------------------------------------
    // 4. Not-null 단언 (!!)
    // -----------------------------------------
    println("\n4. Not-null 단언 (!!):")

    // !! : "나는 이게 null 아닌 거 확실해!" 선언
    // null이면 NPE 발생 → 웬만하면 쓰지 마세요
    val definitelyNotNull: String? = "Hello"
    val len = definitelyNotNull!!.length
    println("   definitelyNotNull!!.length = $len")

    // 주의: null이면 여기서 NPE 터짐
    // val crash = nullStr!!.length  // NPE!

    // -----------------------------------------
    // 5. let으로 null 처리
    // -----------------------------------------
    println("\n5. let으로 null 처리:")

    val name: String? = "홍길동"

    // null이 아닐 때만 실행
    name?.let {
        println("   이름: $it")
        println("   길이: ${it.length}")
    }

    // null이면 아무것도 안 함
    val nullName: String? = null
    nullName?.let {
        println("   이건 출력 안 됨")
    }
    println("   nullName?.let { ... } 실행 안 됨")

    // -----------------------------------------
    // 6. 타입 체크 + 스마트 캐스트
    // -----------------------------------------
    println("\n6. 스마트 캐스트:")

    fun processValue(value: Any?) {
        // null 체크 후에는 non-null로 취급
        if (value != null) {
            println("   value is not null: $value")
        }

        // 타입 체크 후에는 해당 타입으로 취급
        if (value is String) {
            // 자동으로 String으로 캐스트됨
            println("   String 길이: ${value.length}")
        }

        // when과 함께 사용
        when (value) {
            null -> println("   null입니다")
            is Int -> println("   정수: ${value * 2}")
            is String -> println("   문자열: ${value.uppercase()}")
            else -> println("   기타: $value")
        }
    }

    processValue("hello")
    processValue(42)
    processValue(null)

    // -----------------------------------------
    // 7. 실전 예제: API 응답 처리
    // -----------------------------------------
    println("\n7. 실전 예제:")

    data class UserData(
        val id: Long,
        val name: String?,
        val email: String?
    )

    data class ApiResponse(
        val data: UserData?,
        val error: String?
    )

    // 성공 응답
    val successResponse = ApiResponse(
        data = UserData(1, "홍길동", "hong@example.com"),
        error = null
    )

    // 실패 응답
    val errorResponse = ApiResponse(
        data = null,
        error = "User not found"
    )

    fun handleResponse(response: ApiResponse) {
        // 에러가 있으면 먼저 처리
        response.error?.let {
            println("   에러: $it")
            return
        }

        // 데이터 처리
        val user = response.data ?: run {
            println("   데이터 없음")
            return
        }

        // 여기서 user는 non-null
        val displayName = user.name ?: "이름 없음"
        val displayEmail = user.email ?: "이메일 없음"

        println("   사용자: $displayName ($displayEmail)")
    }

    println("   === 성공 응답 ===")
    handleResponse(successResponse)

    println("   === 에러 응답 ===")
    handleResponse(errorResponse)
}
