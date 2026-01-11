package com.practice.jpa.controller

import com.practice.jpa.service.TeamService
import com.practice.jpa.service.VirtualThreadService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * JPA 테스트 컨트롤러
 *
 * 테스트 순서:
 * 1. POST /api/jpa/init - 테스트 데이터 생성
 * 2. GET /api/jpa/n1-problem - N+1 문제 확인
 * 3. GET /api/jpa/fetch-join - Fetch Join 해결
 * 4. GET /api/jpa/entity-graph - Entity Graph 해결
 * 5. GET /api/jpa/batch-size - Batch Size 해결
 */
@RestController
@RequestMapping("/api/jpa")
class JpaTestController(
    private val teamService: TeamService,
    private val virtualThreadService: VirtualThreadService
) {
    /**
     * 테스트 데이터 초기화
     */
    @PostMapping("/init")
    fun initData(): String {
        teamService.initData()
        return "테스트 데이터 생성 완료 (팀 10개, 멤버 50명)"
    }

    /**
     * N+1 문제 발생 케이스
     * 콘솔에서 쿼리 11번 확인 (1 + 10)
     */
    @GetMapping("/n1-problem")
    fun n1Problem(): String {
        val teams = teamService.findAllTeamsWithN1Problem()
        return "N+1 문제 케이스 - 콘솔에서 쿼리 확인! 팀 ${teams.size}개 조회"
    }

    /**
     * Fetch Join으로 해결
     * 콘솔에서 쿼리 1번 확인
     */
    @GetMapping("/fetch-join")
    fun fetchJoin(): String {
        val teams = teamService.findAllTeamsWithFetchJoin()
        return "Fetch Join - 쿼리 1번! 팀 ${teams.size}개 조회"
    }

    /**
     * Entity Graph로 해결
     * 콘솔에서 쿼리 1번 확인
     */
    @GetMapping("/entity-graph")
    fun entityGraph(): String {
        val teams = teamService.findAllTeamsWithEntityGraph()
        return "Entity Graph - 쿼리 1번! 팀 ${teams.size}개 조회"
    }

    /**
     * Batch Size로 해결
     * 콘솔에서 쿼리 2번 확인 (팀 + IN절로 멤버)
     */
    @GetMapping("/batch-size")
    fun batchSize(): String {
        val teams = teamService.findAllTeamsWithBatchSize()
        return "Batch Size - 쿼리 2번! 팀 ${teams.size}개 조회"
    }

    // ===========================================
    // 가상 스레드 테스트
    // ===========================================

    /**
     * 기본 가상 스레드
     */
    @GetMapping("/virtual-thread/basic")
    fun virtualThreadBasic(): String {
        virtualThreadService.basicVirtualThread()
        return "가상 스레드 기본 예제 완료 - 콘솔 확인"
    }

    /**
     * 성능 비교
     */
    @GetMapping("/virtual-thread/compare")
    fun virtualThreadCompare(
        @RequestParam(defaultValue = "1000") taskCount: Int
    ): String {
        virtualThreadService.comparePerformance(taskCount)
        return "성능 비교 완료 - 콘솔에서 결과 확인"
    }

    /**
     * 가상 스레드 정보
     */
    @GetMapping("/virtual-thread/info")
    fun virtualThreadInfo(): String {
        return virtualThreadService.springVirtualThreadInfo()
    }
}
