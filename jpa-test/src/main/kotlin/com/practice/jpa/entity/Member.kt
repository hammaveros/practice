package com.practice.jpa.entity

import jakarta.persistence.*

/**
 * 멤버 엔티티 (다대일의 "다" 쪽 - 연관관계 주인)
 *
 * Member : Team = N : 1
 * - 여러 멤버가 하나의 팀에 소속
 * - 외래키(team_id)를 가지고 있음 → 연관관계의 주인
 */
@Entity
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var username: String,

    var age: Int = 0,

    /**
     * @ManyToOne - 다대일 관계
     *
     * fetch = LAZY (중요!)
     * - 멤버 조회 시 팀 즉시 로딩 X
     * - member.team 접근 시점에 쿼리 실행
     *
     * 주의: ManyToOne의 기본값은 EAGER!
     * - 명시적으로 LAZY 설정 필수
     * - EAGER면 멤버 조회할 때마다 팀도 함께 조회 (N+1 원인)
     *
     * @JoinColumn
     * - 외래키 컬럼명 지정 (team_id)
     * - 생략하면 team_id로 자동 생성되지만 명시 권장
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var team: Team? = null
) {
    /**
     * 팀 변경 (연관관계 편의 메서드)
     *
     * 기존 팀에서 제거하고 새 팀에 추가
     */
    fun changeTeam(newTeam: Team?) {
        // 기존 팀에서 제거
        this.team?.members?.remove(this)

        // 새 팀 설정
        this.team = newTeam

        // 새 팀에 추가
        newTeam?.members?.add(this)
    }

    override fun toString(): String {
        // team 전체 출력하면 순환 참조! team?.name만 출력
        return "Member(id=$id, username='$username', age=$age, team=${team?.name})"
    }
}
