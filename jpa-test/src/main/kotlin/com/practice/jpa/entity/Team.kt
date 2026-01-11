package com.practice.jpa.entity

import jakarta.persistence.*

/**
 * 팀 엔티티 (일대다의 "일" 쪽)
 *
 * Team : Member = 1 : N
 * - 하나의 팀에 여러 멤버가 소속
 * - 연관관계의 주인: Member.team (외래키가 있는 쪽)
 */
@Entity
class Team(
    // ===========================================
    // 기본 필드
    // ===========================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String,

    // ===========================================
    // 연관관계 (일대다)
    // ===========================================
    /**
     * mappedBy = "team"
     * - 연관관계의 주인이 아님 (읽기 전용)
     * - Member 엔티티의 team 필드와 매핑
     *
     * fetch = LAZY (기본값이지만 명시)
     * - 팀 조회 시 멤버 즉시 로딩 X
     * - members 접근 시점에 쿼리 실행 (지연 로딩)
     *
     * cascade = ALL
     * - 팀 저장/삭제 시 멤버도 함께 저장/삭제
     * - 팀이 멤버의 생명주기를 관리
     *
     * orphanRemoval = true
     * - 고아 객체 자동 삭제
     * - team.members.remove(member) 하면 member도 DB에서 삭제
     */
    @OneToMany(
        mappedBy = "team",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val members: MutableList<Member> = mutableListOf()
) {
    // ===========================================
    // 연관관계 편의 메서드
    // ===========================================
    /**
     * 멤버 추가 (양방향 관계 설정)
     *
     * 왜 필요한가?
     * - 양방향 관계에서 한쪽만 설정하면 불일치 발생
     * - team.members.add(member) 만 하면 member.team은 null
     * - 편의 메서드로 양쪽 다 설정
     */
    fun addMember(member: Member) {
        members.add(member)
        member.team = this
    }

    /**
     * 멤버 제거
     */
    fun removeMember(member: Member) {
        members.remove(member)
        member.team = null
    }

    override fun toString(): String {
        // members 출력하면 순환 참조 발생 주의!
        return "Team(id=$id, name='$name', memberCount=${members.size})"
    }
}
