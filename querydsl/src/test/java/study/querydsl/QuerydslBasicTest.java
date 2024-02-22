package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.Dto.MemberDto;
import study.querydsl.Dto.QMemberDto;
import study.querydsl.Dto.UserDto;
import study.querydsl.Entity.Member;
import study.querydsl.Entity.QMember;
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

    @Test //세타 조인, 회원의 이름이 팀 이름과 같은 회원 조회 (세타 조인 : 모든 것을 막 조회) - 외부 조인이 불가능하다
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
    @Test //예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
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

    @PersistenceUnit
    EntityManagerFactory emf;
    @Test //페치 조인 없음
    public void Test_fetch_join_no() {
        em.flush();
        em.clear();

        Member result = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(result.getTeam());
        org.assertj.core.api.Assertions.assertThat(loaded).as("페치 조인 미적용").isFalse();

    }

    @Test //페치 조인 활용
    public void Test_fetch_join() {
        em.flush();
        em.clear();

        Member result = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(result.getTeam());
        org.assertj.core.api.Assertions.assertThat(loaded).as("페치 조인 미적용").isTrue();
    }

    /**
     * 나이가 가장 많은 회원 조회
     */
    @Test //서브 쿼리
    public void Test_SubQuery() {
        QMember memberSub = new QMember("memberSub"); //바깥에 있는 멤버와 겹치면 안되므로 정의
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions //서브 쿼리
                                .select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();
        org.assertj.core.api.Assertions.assertThat(result)
                .extracting("age")
                .containsExactly(40);
    }
    /**
     * 나이가 평균 이상인 회원 조회
     */
    @Test //서브 쿼리
    public void Test_SubQueryGoe() {
        QMember memberSub = new QMember("memberSub"); //바깥에 있는 멤버와 겹치면 안되므로 정의
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe( //greater or equal
                        JPAExpressions //서브 쿼리
                                .select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();
        org.assertj.core.api.Assertions.assertThat(result)
                .extracting("age")
                .containsExactly(30,40);
    }

    /**
     * 나이가 평균 이상인 회원 조회
     */
    @Test //서브 쿼리
    public void Test_SubQueryIn() {
        QMember memberSub = new QMember("memberSub"); //바깥에 있는 멤버와 겹치면 안되므로 정의
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in( //뭐뭐 이고, 뭐뭐 인 것
                        JPAExpressions //서브 쿼리
                                .select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                ))
                .fetch();
        org.assertj.core.api.Assertions.assertThat(result)
                .extracting("age")
                .containsExactly(20, 30, 40);
    }

    @Test //셀렉트에서의 서브 쿼리
    public void Test_selectSubQuery() {
        QMember memberSub = new QMember("memberSub");
        List<Tuple> result = queryFactory
                .select(member.username,
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub)
                )
                .from(member)
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }
    //서브 쿼리는 from 절에서 사용할 수 없다는 단점이 있다
    //해결 방안 1.서브쿼리를 join으로 변경 2. 애플리케이션을 쿼리 2번 분리해서 실행 3. nativeSQL을 사용한다

    @Test //Case 문
    public void Test_basicCase() {
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
    
    @Test
    public void Test_compleCase() {
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
    
    @Test //상수 더하기
    public void Test_constant() {
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }
    
    @Test //문자 더하기
    public void Test_concat() {
        List<String> result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test //프로젝션 : 셀렉트 대상 지정 //한 개의 대상 찾기
    public void Test_simpleProjection() {
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test //두 개의 대상 찾기
    public void Test_tupleProjection() {
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();
        for (Tuple tuple : result) {
            String s = tuple.get(member.username);
            Integer i = tuple.get(member.age);
            System.out.println("i = " + i);
            System.out.println("s = " + s);
        }
    }

    @Test //세 개 이상 대상 찾기 (DTO를 통하여) //JPQL 버전
    public void Test_findDtoByJPQL() {
        List<MemberDto> resultList =
                em.createQuery("select new study.querydsl.Dto.MemberDto(m.username, m.age) from Member m", MemberDto.class)
                .getResultList();

        for (MemberDto memberDto : resultList) {
            System.out.println("memberDto = " + memberDto);
        }
    }
    @Test //세 개 이상 대상 찾기 (DTO의 getter, setter를 통하여) //queryDSL 버전
    public void Test_findDtoByQueryDsl_Setter() {
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class, member.username, member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }
    @Test //세 개 이상 대상 찾기 (DTO의 필드를 통하여) //DTO에 게터 세터가 없어도 알아서 필드에 꽂는다
    public void Test_findDtoByField() {
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class, member.username, member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }
    
    @Test //세 개 이상 대상 찾기 (DTO의 생성자를 통하여)
    public void Test_findDtoByConstructor() {
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class, member.username, member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test //세 개 이상 대상 찾기 (User DTO를 통하여)
    public void Test_findUserDto_Field() { //username이 아닌 name이 dto에 변수명으로 들어가 있는 경우이다
        QMember memberSub = new QMember("memberSub");
        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.username.as("name"), //이름이 다르므로 name으로 맞춰준다
                        ExpressionUtils.as(JPAExpressions //서브 쿼리
                                .select(memberSub.age.max()) //최대 나이로 찍고 싶으면
                                .from(memberSub),"age") //서브 쿼리이기에 이름이 없으므로 age로 맞춰준다
                        ))
                .from(member)
                .fetch();
        for (UserDto userDto : result) {
            System.out.println("memberDto = " + userDto);
        }
    }

    @Test //세 개 이상 대상 찾기 (DTO의 생성자를 통하여) //필드 네임과 상관없음으로 as로 이름 맞추는 것은 필요하지 않다
    public void Test_findUserDto_Constructor() {
        List<UserDto> result = queryFactory
                .select(Projections.constructor(UserDto.class, member.username, member.age))
                .from(member)
                .fetch();
        for (UserDto userDto : result) {
            System.out.println("memberDto = " + userDto);
        }
    }
    
    @Test //@QueryProjection
    public void Test_findDtoByQueryProjection() { //queryDSL에 의존성을 가지게 되는 것이 단점이다
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age/*member.id*/))
                //컴파일 시점이 아닌 코드에서 오류를 찾을 수 있다는 장점이 있다
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test//동적 쿼리 - booleanbuilder 사용
    public void Test_dynamicQuery_BooleanBuilder() {
        String usernameParam = "member1";
        Integer ageParam = null;

        List<Member> result = searchMember1(usernameParam, ageParam);
        org.assertj.core.api.Assertions.assertThat(result.size()).isEqualTo(1);
    }
    
    @Test //동적 쿼리 - where 다중 파라미터 사용 (메서드로 뽑으면 조립이 가능하고, 메서드를 재활용할 수도 있다)
    public void Test_dynamicQuery_WhereParam() {
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember2(usernameParam, ageParam);
        org.assertj.core.api.Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test //대용량 벌크 연산
    public void Test_bulkUpdate() {
        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();
        //member1 = 비회원
        //member2 = 비회원
        //member3 = 30
        //member4 = 40
        //영속성 컨텍스트를 무시하고 업데이트를 하기 때문에 DB와 맞지 않다
        //영속성 컨텍스트가 우선이므로 DB에서 가져온 애를 버리게 된다
        em.flush();
        em.clear();
        //초기화를 통해 DB와 영속성 컨텍스트를 맞춰주어야 한다
        List<Member> result = queryFactory.selectFrom(member).fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    @Test //bulk 기존 숫자에 더하기, 곱하기
    public void Test_bulkAdd() {
        long count = queryFactory
                .update(member)
                .set(member.age, member.age.add(-1))
//                .set(member.age, member.age.multiply(2))
                .execute();
    }

    @Test //bulk delete
    public void Test_bulkDelete() {
        long count = queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute();
    }
    
    @Test //SQL Function
    public void Test_sqlFunction() {
        List<String> result = queryFactory
                .select(Expressions.stringTemplate(
                        "function('replace', {0}, {1}, {2})", member.username, "member", "M"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
    
    @Test
    public void Test_sqlFunction2() {
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
//                .where(member.username.eq(Expressions.stringTemplate(
//                        "function('lower', {0})", member.username
//                )))
                .where(member.username.eq(member.username.lower()))
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ메서드ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    private List<Member> searchMember1(String usernameParam, Integer ageParam) {
        BooleanBuilder builder = new BooleanBuilder(/*member.username.eq(usernameParam)*/ /*방어 코드 (비어있으면 안될 때)*/);
        if(usernameParam != null) {
            builder.and(member.username.eq(usernameParam));
        }
        if(ageParam != null) {
            builder.and(member.age.eq(ageParam));
        }
        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }

    private List<Member> searchMember2(String usernameParam, Integer ageParam) {
        return queryFactory
                .selectFrom(member)
                .where(allEq(usernameParam, ageParam))
                .fetch();
    }
    private BooleanExpression usernameEq(String usernameParam) {
        return usernameParam != null ? member.username.eq(usernameParam) : null;
    }
    private BooleanExpression ageEq(Integer ageParam) {
        return ageParam != null ? member.age.eq(ageParam) : null;
    }

    private BooleanExpression allEq(String usernameParam, Integer ageParam) { //조립이 가능하다
        return usernameEq(usernameParam).and(ageEq(ageParam));
    }
}

