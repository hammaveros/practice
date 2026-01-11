package com.practice.jpa.service

import com.practice.jpa.entity.Member
import com.practice.jpa.entity.Team
import com.practice.jpa.repository.TeamRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 팀 서비스
 *
 * N+1 문제 재현과 해결책 비교
 */
@Service
@Transactional(readOnly = true)
class TeamService(
    private val teamRepository: TeamRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    // ===========================================
    // 테스트 데이터 생성
    // ===========================================
    @Transactional
    fun initData() {
        // 팀 10개, 각 팀에 멤버 5명
        repeat(10) { teamIdx ->
            val team = Team(name = "Team-${teamIdx + 1}")

            repeat(5) { memberIdx ->
                val member = Member(
                    username = "Member-${teamIdx + 1}-${memberIdx + 1}",
                    age = 20 + memberIdx
                )
                team.addMember(member)
            }

            teamRepository.save(team)
        }

        log.info("=== 테스트 데이터 생성 완료: 팀 10개, 멤버 50명 ===")
    }

    // ===========================================
    // N+1 문제 발생 케이스
    // ===========================================
    /**
     * N+1 문제 발생!
     *
     * 실행되는 쿼리:
     * 1. SELECT * FROM team (1번)
     * 2. SELECT * FROM member WHERE team_id = 1 (1번)
     * 3. SELECT * FROM member WHERE team_id = 2 (1번)
     * ... (N번)
     *
     * → 총 N+1번 쿼리
     */
    fun findAllTeamsWithN1Problem(): List<Team> {
        log.info("=== N+1 문제 발생 케이스 ===")

        val teams = teamRepository.findAll()  // 1번 쿼리

        teams.forEach { team ->
            // members 접근 시마다 쿼리 발생 (N번)
            log.info("팀: ${team.name}, 멤버 수: ${team.members.size}")
        }

        return teams
    }

    // ===========================================
    // 해결책 1: Fetch Join
    // ===========================================
    /**
     * Fetch Join으로 해결
     *
     * 실행되는 쿼리:
     * SELECT t.*, m.* FROM team t
     * JOIN member m ON t.id = m.team_id
     *
     * → 쿼리 1번으로 해결!
     */
    fun findAllTeamsWithFetchJoin(): List<Team> {
        log.info("=== Fetch Join 해결 ===")

        val teams = teamRepository.findAllWithMembersFetchJoin()  // 1번 쿼리

        teams.forEach { team ->
            // 이미 로딩됨, 추가 쿼리 없음
            log.info("팀: ${team.name}, 멤버 수: ${team.members.size}")
        }

        return teams
    }

    // ===========================================
    // 해결책 2: Entity Graph
    // ===========================================
    /**
     * Entity Graph로 해결
     */
    fun findAllTeamsWithEntityGraph(): List<Team> {
        log.info("=== Entity Graph 해결 ===")

        val teams = teamRepository.findAllWithMembersEntityGraph()

        teams.forEach { team ->
            log.info("팀: ${team.name}, 멤버 수: ${team.members.size}")
        }

        return teams
    }

    // ===========================================
    // 해결책 3: Batch Size (기본 설정 적용)
    // ===========================================
    /**
     * Batch Size로 해결
     *
     * application.yml에서 default_batch_fetch_size: 100 설정
     *
     * 실행되는 쿼리:
     * 1. SELECT * FROM team (1번)
     * 2. SELECT * FROM member WHERE team_id IN (1, 2, 3, ..., 10) (1번)
     *
     * → 쿼리 2번으로 해결!
     * (팀이 100개 넘으면 100개씩 나눠서 추가 쿼리)
     */
    fun findAllTeamsWithBatchSize(): List<Team> {
        log.info("=== Batch Size 해결 ===")

        val teams = teamRepository.findAll()  // 1번 쿼리

        // members 접근 시 IN 절로 한번에 조회
        teams.forEach { team ->
            log.info("팀: ${team.name}, 멤버 수: ${team.members.size}")
        }

        return teams
    }
}
