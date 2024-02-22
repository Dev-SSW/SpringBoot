package study.querydsl.Repository.Jpa;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.Dto.MemberSearchCondition;
import study.querydsl.Dto.MemberTeamDto;
import study.querydsl.Entity.Member;
import study.querydsl.Entity.Team;
import study.querydsl.Repository.Jpa.MemberJpaRepository;

import java.util.List;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test //순수 JPA
    public void Test_basic() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).get();
        Assertions.assertThat(findMember).isEqualTo(member);

        List<Member> all1 = memberJpaRepository.findAll();
        Assertions.assertThat(all1).containsExactly(member);

        List<Member> all2 = memberJpaRepository.findByUsername("member1");
        Assertions.assertThat(all2).containsExactly(member);
    }
    
    @Test //QueryDsl
    public void Test_basic_Querydsl() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).get();
        Assertions.assertThat(findMember).isEqualTo(member);

        List<Member> all1 = memberJpaRepository.findAll_Querydsl();
        Assertions.assertThat(all1).containsExactly(member);

        List<Member> all2 = memberJpaRepository.findByUsername_Querydsl("member1");
        Assertions.assertThat(all2).containsExactly(member);
    }
    
    @Test //동적 쿼리와 성능 최적화 조회 - builder 사용
    public void Test_Search() {
        //init
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition memberSearchCondition = new MemberSearchCondition();
        memberSearchCondition.setAgeGoe(35); //AgeGoe 35로 설정
        memberSearchCondition.setAgeLoe(40); //AgeLoe 40로 설정
        memberSearchCondition.setTeamName("teamB");
        //만약 조건이 없다면? -> 모든 멤버를 DB에서 끌어 온다 -> 성능 상의 이슈가 있을 수 있기에 페이징 처리 등이 필요하다.

        List<MemberTeamDto> memberTeamDtos = memberJpaRepository.searchByBuilder(memberSearchCondition);
        Assertions.assertThat(memberTeamDtos).extracting("username").containsExactly("member4");


    }
}