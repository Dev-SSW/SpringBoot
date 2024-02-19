package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.Entity.Member;
import study.querydsl.Entity.QMember;
import study.querydsl.Entity.QTeam;
import study.querydsl.Entity.Team;

import java.util.List;

import static study.querydsl.Entity.QMember.*;
import static study.querydsl.Entity.QTeam.team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    //시작 전 init
    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
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
    }

    //jpql
    @Test
    public void Test_startJPQL() {
        //member1을 찾아라
        String qlString =
                "select m from Member m " +
                "where m.username = :username";

        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        Assertions.assertEquals(findMember.getUsername(),"member1");
    }

    //queryDSL
    @Test
    public void Test_startQueryDSL() {
        //QMember m = new QMember("m"); //"m"은 어떤 QMember인지 구분을 하기 위해 이름을 준다
        //QMember m = QMember.member; //위 코드의 두번째 방법
        //QMember.member 후 Alt + Enter로 스태틱 임포트를 통해 사용할 수 있다 m -> QMember.member -> member로 바꾼 것임
        //같은 테이블을 조인해서 사용해야하는 경우에는 "QMember m = new QMember("m")" 이런 식으로 지정해주면 된다
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1")) //파라미터 바인딩 처리
                .fetchOne();

        Assertions.assertEquals(findMember.getUsername(),"member1");
    }

    @Test
    public void Test_search() { //검색
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                        //쉼표로 and 처리 가능
                )
                .fetchOne();

        Assertions.assertEquals(findMember.getUsername(), "member1");
    }

    @Test
    public void Test_resultFetch() { //조회 방법들
//        List<Member> fetch = queryFactory //리스트로 뽑아온다
//                .selectFrom(member)
//                .fetch();
//
//        Member fetchOne = queryFactory //하나만
//                .selectFrom(member)
//                .fetchOne();
//
//        Member fetchFirst = queryFactory //맨 앞에 하나만
//                .selectFrom(member)
//                .fetchFirst();// = .limit(1).fetchOne()

        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();
        results.getTotal(); //페이징을 위한 total count를 가져온다
        List<Member> content = results.getResults(); //내용을 가져온다

        long total = queryFactory //count 쿼리만 가져온다
                .selectFrom(member)
                .fetchCount();
    }

     /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순 (desc)
     * 2. 회원 이름 올림차순 (asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     **/
    @Test
    public void Test_sort() { //정렬
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);
        Assertions.assertEquals(member5.getUsername(), "member5");
        Assertions.assertEquals(member6.getUsername(), "member6");
        Assertions.assertNull(memberNull.getUsername());
    }

    @Test
    public void Test_Paging1() { //페이징
        QueryResults<Member> queryResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();
                //.fetch();
        Assertions.assertEquals(queryResults.getTotal(), 4);
        Assertions.assertEquals(queryResults.getLimit(), 2);
        Assertions.assertEquals(queryResults.getOffset(), 1);
        Assertions.assertEquals(queryResults.getResults().size(), 2);
    }

    @Test
    public void Test_aggregation() { //집합
        List<Tuple> result = queryFactory
                .select(member.count(), member.age.sum(), member.age.avg(), member.age.max(), member.age.min())
                .from(member)
                .fetch();
        Tuple tuple = result.get(0);

        Assertions.assertEquals(tuple.get(member.count()), 4);
        Assertions.assertEquals(tuple.get(member.age.sum()), 100); //10, 20, 30, 40
        Assertions.assertEquals(tuple.get(member.age.avg()), 25);
        Assertions.assertEquals(tuple.get(member.age.max()), 40);
        Assertions.assertEquals(tuple.get(member.age.min()), 10);
    }

    /**
     * 팀의 이름과 각 팀의 평균 연령을 구해라
     */
    @Test
    public void group() throws Exception {
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team) //member.team을 team이라는 약어로 사용
                .groupBy(team.name) //팀의 이름으로 그룹핑
                .fetch();
        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        Assertions.assertEquals(teamA.get(team.name), "teamA");
        Assertions.assertEquals(teamA.get(member.age.avg()), 15); //(10 + 20) / 2

        Assertions.assertEquals(teamB.get(team.name), "teamB");
        Assertions.assertEquals(teamB.get(member.age.avg()), 35); //(30 + 40) / 2
    }

    /**
     * 팀A에 소속된 모든 회원을 찾아라.
     */
    @Test //조인
    public void Test_join() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .leftJoin(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        org.assertj.core.api.Assertions.assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");
    }

    //세타 조인, 회원의 이름이 팀 이름과 같은 회원 조회 (세타 조인 : 모든 것을 막 조회) - 외부 조인이 불가능하다
    @Test
    public void Test_theta_join() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Member> result = queryFactory
                .select(member)
                .from(member, team) //모든 멤버와 모든 팀에서 다 조인을 해버린다
                .where(member.username.eq(team.name))
                .fetch();

        org.assertj.core.api.Assertions.assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

    //1. 조인 대상 필터링
    //예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
    @Test //JPQL : select m, t from Member m left join m.team t on t.name = "teamA"
    public void Test_On_filtering() {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA")) //member와 team에서 member의 모든 테이블과 team.name이 teamA인 team의 테이블을 출력
                .fetch(); //.join이라면 팀 명이 null인 멤버는 출력되지 않는다
                //on 절에서 필터링 하는 것은 where에서 필터링 하는 것과 기능이 동일하다
                // = .join(member.team, team).where(team.name.eq("teamA"))
                //이너 조인이라면 where을 사용하고, 정말 외부 조인이 필요한 경우 사용한다
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }
    //2. 연관관계 없는 엔티티 외부 조인
    //예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
    @Test
    public void Test_On_no_relation() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team) //보통은 (member.team, team) 이었지만 문법이 다르다 (세타 조인과 다르다) (세타 조인은 leftjoin이 불가능했다)
                //(member.team, team)과 다르게 id로 매칭이 되지 않기 때문에 이름으로만 조인 대상이 필터링된다
                .on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }
}
