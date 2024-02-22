package study.querydsl.Repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.Dto.MemberSearchCondition;
import study.querydsl.Dto.MemberTeamDto;
import study.querydsl.Entity.Member;
import study.querydsl.Entity.QMember;
import study.querydsl.Entity.Team;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static study.querydsl.Entity.QMember.member;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    EntityManager em;

    @Autowired MemberRepository memberRepository;

    @Test //순수 JPA
    public void Test_basic() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).get();
        Assertions.assertThat(findMember).isEqualTo(member);

        List<Member> all1 = memberRepository.findAll();
        Assertions.assertThat(all1).containsExactly(member);

        List<Member> all2 = memberRepository.findByUsername("member1");
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

        List<MemberTeamDto> memberTeamDtos = memberRepository.search(memberSearchCondition);
        Assertions.assertThat(memberTeamDtos).extracting("username").containsExactly("member4");
    }

    @Test //querydslPredicateExecutor //한계가 명확하다 (left join 불가능, querydsl에 의존)
    public void Test_querydslPredicateExecutor() {
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
        
        Iterable<Member> result = memberRepository.findAll(member.age.between(10, 40).and(member.username.eq("member1")));
        for (Member findMember : result) {
            System.out.println("findMember = " + findMember);
        }
    }


}