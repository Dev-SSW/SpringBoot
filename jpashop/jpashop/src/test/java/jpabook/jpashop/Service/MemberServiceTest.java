package jpabook.jpashop.Service;

import jpabook.jpashop.Domain.Member;
import jpabook.jpashop.Repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

//@RunWith(SpringRunner.class) //JUnit 4 방식, 5에서는 생략한다
@SpringBootTest //스프링부트를 띄우고 테스트
@Transactional //변경을 위해서는 트랜잭션 안에서 동작해야 한다, 반복 가능한 테스트 지원,
//각각의 테스트를 실행할 때마다 트랜잭션 실행 후 테스트가 끝나면 자동으로 롤백 (테스트 어노테이션에서만 롤백 지원)
public class MemberServiceTest {
    @Autowired MemberRepository memberRepository; //서로가 서로를 사용하기 위해 의존관계를 주입해주는 Autowired (스프링 빈에 등록한다라고 한다)
    @Autowired MemberService memberService;
    @Test
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim"); //Member를 하나 만들어서 이름을 kim으로 설정
        //when
        Long saveID = memberService.join(member); //만들어진 member를 회원가입시키고 (영속화) 리턴값을 saveID에 저장)
        //then
        Assertions.assertEquals(member, memberRepository.findOne(saveID)); //현재의 member와 영속화된 member가 같은지 확인
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim"); //멤버를 생성 후 kim으로 설정

        Member member2 = new Member();
        member2.setName("kim"); //멤버를 하나 더 생성 후 같은 이름으로 설정

        //when
        memberService.join(member1);
        //then
        Assertions.assertThrows(IllegalStateException.class, ()-> {memberService.join(member2);});
        //JUnit5 방식
        //멤버 2를 조인했을 때 예외가 발생해야 한다.
    }


    //아래는 간단한 예시, 새로 만든 MemberRepository로 인해 주석 처리
    // public void testMember() throws Exception {
    //given
    //Member member = new Member();
    //member.setName("memberA"); //멤버 하나 이름을 memberA로 설정
    //when
    //Long saveId = memberRepository.save(member); //member를 영속화
    //Member findMember = memberRepository.find(saveId); //member id를 통해 member 찾기
    //then
    //Assertions.assertThat(findMember.getId()).isEqualTo(member.getId()); //가져온 것과 member의 아이디가 일치하는지
    //Assertions.assertThat(findMember.getName()).isEqualTo(member.getName()); //가져온 것과 member의 이름이 일치하는지
    //Assertions.assertThat(findMember).isEqualTo(member); //동일한지를 확인
    //}
}