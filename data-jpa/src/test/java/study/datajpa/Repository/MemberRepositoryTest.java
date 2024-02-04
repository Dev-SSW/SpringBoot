package study.datajpa.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.Dto.MemberDto;
import study.datajpa.Entity.Member;
import study.datajpa.Entity.Team;
import study.datajpa.Repository.Jpa.MemberQueryRepository;
import study.datajpa.Repository.DataJpa.MemberRepository;
import study.datajpa.Repository.DataJpa.TeamRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;
    @Autowired MemberQueryRepository memberQueryRepository;

    @Test
    public void Test_Member() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        Assertions.assertEquals(findMember.getId(), member.getId());
        Assertions.assertEquals(findMember.getUsername(), member.getUsername());
        Assertions.assertEquals(findMember, member);
    }

    @Test
    public void Test_basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        Assertions.assertEquals(findMember1, member1);
        Assertions.assertEquals(findMember2, member2);

        //findMember1.setUsername("member!!!!"); //변경 감지를 통한 업데이트
        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertEquals(all.size(), 2);

        //카운트 검증
        long count = memberRepository.count();
        Assertions.assertEquals(count, 2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        Assertions.assertEquals(deletedCount, 0);
    }

    @Test
    public void Test_findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("aaa", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("aaa", 15);

        Assertions.assertEquals(result.get(0).getUsername(), "aaa");
        Assertions.assertEquals(result.get(0).getAge(), 20);
        Assertions.assertEquals(result.size(), 1);

    }
//    @Test
//    public void findHelloBy() {
//        List<Member> helloBy = memberRepository.findHelloBy();
//    }

    @Test
    public void Test_nameQuery() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("aaa");
        Member findMember = result.get(0);
        Assertions.assertEquals(findMember, m1);
    }

    @Test
    public void Test_Query() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("aaa", 10);
        Member findMember = result.get(0);
        Assertions.assertEquals(findMember, m1);
    }

    @Test
    public void Test_findUsernameList() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void Test_findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("aaa", 10);
        memberRepository.save(m1);
        m1.setTeam(team);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("s = " + dto);
        }
    }

    @Test
    public void Test_findByNames() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("aaa", "bbb"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void Test_returnType() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> lista = memberRepository.findListByUsername("aaa");
        Member membera = memberRepository.findMemberByUsername("aaa");
        Optional<Member> optionala= memberRepository.findOptionalByUsername("aaa");
        System.out.println("optionala = " + optionala);
        System.out.println("membera = " + membera);
        System.out.println("lista = " + lista);

        List<Member> result1 = memberRepository.findListByUsername("asdasd");
        System.out.println("result = " + result1); //empty로 나온다 (if = null)과 같은 조건문 없이 확인 할 수 있다
        Member result2 = memberRepository.findMemberByUsername("asdasd");
        System.out.println("result2 = " + result2); //null로 나온다 (단건 조회에서)
        //데이터가 있을 수도 있고 없을 수도 있으면 Optional을 사용하는게 좋다
    }

    @Test
    public void Test_paging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //api 컨트롤러에서 page를 그대로 쓰면 안된다(엔티티 스펙 노출), dto로 받는 방법 ->
        //page.map(member -> {new MemberDto(member.getId(), member.getUsername(), (teamName))})

        //페이지를 사용하면 totalCount를 자동으로 반환해준다
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements(); //totalCount와 같은 함수

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        Assertions.assertEquals(content.size(), 3); //가져온 페이지 개수
        Assertions.assertEquals(totalElements, 5); //페이지 총 개수
        Assertions.assertEquals(page.getNumber(), 0); //페이지 번호를 가져올 수 있다
        Assertions.assertEquals(page.getTotalPages(), 2); //전체 페이지 개수가 2개 남았다
        Assertions.assertTrue(page.isFirst()); //첫번째 페이지인지
        Assertions.assertTrue(page.hasNext()); //다음 페이지가있는지
    }

    @Test
    public void Test_bulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        int resultCount = memberRepository.bulkAgePlus(20);
//      em.clear();
//      영속성 컨텍스트를 모두 날려버리고 이 다음 코드에서 DB에서 가져오기 때문에 벌크 연산이 영속성 컨텍스트를 무시하는 문제를 해결

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5); //영속성 컨텍스트를 무시하기에 DB에만 반영이 되어있는 문제가 발생할 수 있다
                                                    //영속성 컨텍스트를 초기화 해줘야 한다
        Assertions.assertEquals(resultCount, 3);
    }

    @Test
    public void Test_findMemberLazy() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

//        List<Member> members = memberRepository.findAll();
//        List<Member> members = memberRepository.findMemberFetchJoin(); //페치 조인 설정
//        List<Member> members = memberRepository.findAll(); //EntityGraph 사용
        List<Member> members =memberRepository.findEntityGraphByUsername("member1"); //member1만 페치 조인 된 것이다

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            //프록시로 멤버에서 사용됨 -> 페치 조인 설정을 통해 프록시가 아닌 실제 엔티티가 사용됨
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            //페치 조인이 필요한 상황 (getName을 끌어오기 위해 Team을 쿼리를 날려 가져옴) = 1 + N 문제, -> 페치 조인으로 해결
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void Test_queryHint() {
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush();
        em.clear();

        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush(); //Update 쿼리 (변경 감지)
    }

    @Test
    public void Test_lock() {
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush();
        em.clear();

        List<Member> reuslt = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void Test_Custom() {
        List<Member> result =memberRepository.findMemberCustom();
    }
}