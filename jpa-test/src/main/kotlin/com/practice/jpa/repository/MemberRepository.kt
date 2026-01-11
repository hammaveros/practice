package com.practice.jpa.repository

import com.practice.jpa.entity.Member
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * 멤버 레포지토리
 *
 * ManyToOne 관계에서의 N+1 해결 예시
 */
interface MemberRepository : JpaRepository<Member, Long> {

    // ===========================================
    // 1. 기본 조회 (ManyToOne은 LAZY로 설정했으니 안전)
    // ===========================================
    // findAll() 사용 시:
    // - Member만 조회 (Team은 프록시)
    // - member.team.name 접근 시 추가 쿼리

    // ===========================================
    // 2. Fetch Join (N:1은 페이징 가능!)
    // ===========================================
    /**
     * ManyToOne은 페이징과 함께 Fetch Join 가능
     *
     * OneToMany와 다르게:
     * - 데이터 뻥튀기 없음 (N쪽이 기준이라)
     * - 페이징 안전하게 동작
     */
    @Query("SELECT m FROM Member m JOIN FETCH m.team")
    fun findAllWithTeamFetchJoin(): List<Member>

    /**
     * 조건 + Fetch Join
     */
    @Query("SELECT m FROM Member m JOIN FETCH m.team WHERE m.age > :age")
    fun findByAgeGreaterThanWithTeam(age: Int): List<Member>

    // ===========================================
    // 3. Entity Graph
    // ===========================================
    @EntityGraph(attributePaths = ["team"])
    @Query("SELECT m FROM Member m")
    fun findAllWithTeamEntityGraph(): List<Member>

    /**
     * 이름으로 조회 + Team 함께
     */
    @EntityGraph(attributePaths = ["team"])
    fun findByUsername(username: String): List<Member>
}
