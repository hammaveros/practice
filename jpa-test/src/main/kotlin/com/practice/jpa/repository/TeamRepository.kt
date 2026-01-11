package com.practice.jpa.repository

import com.practice.jpa.entity.Team
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * 팀 레포지토리
 *
 * N+1 문제 해결을 위한 다양한 방법 예시
 */
interface TeamRepository : JpaRepository<Team, Long> {

    // ===========================================
    // 1. 기본 조회 (N+1 발생!)
    // ===========================================
    /**
     * findAll() 사용 시:
     * 1. SELECT * FROM team (1번)
     * 2. 각 팀의 members 접근 시 SELECT * FROM member WHERE team_id = ? (N번)
     * → 총 N+1 쿼리 발생
     */
    // 기본 제공되는 findAll() 사용

    // ===========================================
    // 2. Fetch Join (JPQL)
    // ===========================================
    /**
     * JOIN FETCH로 연관 엔티티 한번에 조회
     *
     * 장점:
     * - 쿼리 1번으로 해결
     * - 가장 직관적
     *
     * 단점:
     * - JPQL 직접 작성 필요
     * - 페이징 불가 (컬렉션 페치 조인 시)
     * - 둘 이상의 컬렉션 페치 조인 불가
     */
    @Query("SELECT DISTINCT t FROM Team t JOIN FETCH t.members")
    fun findAllWithMembersFetchJoin(): List<Team>

    // ===========================================
    // 3. Entity Graph
    // ===========================================
    /**
     * @EntityGraph로 함께 조회할 연관 엔티티 지정
     *
     * 장점:
     * - JPQL 없이 간단하게 사용
     * - 메서드 이름으로 조회 조건 지정 가능
     *
     * 단점:
     * - 복잡한 조건은 어려움
     * - outer join으로 동작 (데이터 중복 가능)
     *
     * attributePaths: 함께 조회할 연관 엔티티
     * type:
     * - FETCH: 지정한 것만 EAGER, 나머지 LAZY
     * - LOAD: 지정한 것만 EAGER, 나머지는 엔티티 설정 따름
     */
    @EntityGraph(attributePaths = ["members"], type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT t FROM Team t")
    fun findAllWithMembersEntityGraph(): List<Team>

    /**
     * 메서드 이름으로 조회 + Entity Graph
     */
    @EntityGraph(attributePaths = ["members"])
    fun findEntityGraphByName(name: String): Team?

    // ===========================================
    // 4. Batch Size (application.yml에서 설정)
    // ===========================================
    /**
     * Batch Size는 별도 메서드 필요 없음
     * application.yml의 default_batch_fetch_size 설정으로 동작
     *
     * findAll() 호출 후 members 접근 시:
     * - Batch Size 100이면
     * - SELECT * FROM member WHERE team_id IN (1, 2, 3, ... 100)
     * - 100개씩 묶어서 IN 절로 조회
     */
}
