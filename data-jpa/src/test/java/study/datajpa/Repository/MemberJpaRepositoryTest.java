package study.datajpa.Repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.Entity.Member;
import study.datajpa.Repository.Jpa.MemberJpaRepository;

import java.util.List;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository memberJpaRepository;
    @Test
    public void Test_Member() {
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(savedMember.getId());

        Assertions.assertEquals(findMember.getId(), member.getId());
        Assertions.assertEquals(findMember.getUsername(), member.getUsername());
        Assertions.assertEquals(findMember, member);
    }

    @Test
    public void Test_basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        Assertions.assertEquals(findMember1, member1);
        Assertions.assertEquals(findMember2, member2);

        //findMember1.setUsername("member!!!!"); //변경 감지를 통한 업데이트
        //리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        Assertions.assertEquals(all.size(), 2);

        //카운트 검증
        long count = memberJpaRepository.count();
        Assertions.assertEquals(count, 2);

        //삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deletedCount = memberJpaRepository.count();
        Assertions.assertEquals(deletedCount, 0);
    }

    @Test
    public void Test_findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("aaa", 20);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("aaa", 15);

        Assertions.assertEquals(result.get(0).getUsername(), "aaa");
        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(result.get(0).getAge(), 20);
    }

    @Test
    public void Test_NamedQuery() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsername("aaa");
        Member findMember = result.get(0);
        Assertions.assertEquals(findMember, m1);
    }

    @Test
    public void Test_paging() {
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        Assertions.assertEquals(members.size(), 3); //페이징을 했으니 3개
        Assertions.assertEquals(totalCount, 5); //totalCount는 전부이므로 5개
    }

    @Test
    public void Test_bulkUpdate() {
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 19));
        memberJpaRepository.save(new Member("member3", 20));
        memberJpaRepository.save(new Member("member4", 21));
        memberJpaRepository.save(new Member("member5", 40));

        int resultCount = memberJpaRepository.bulkAgePlus(20);

        Assertions.assertEquals(resultCount, 3);
    }
}