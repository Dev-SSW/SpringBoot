package jpabook.jpashop.Service;

import jpabook.jpashop.Repository.MemberRepository;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //읽기 전용이라고 말해줌으로서 최적화 (읽기 전용인 것에만 추가)
@RequiredArgsConstructor //final 필드만을 가지고 생성자를 만들어준다. (롬복 지원)
public class MemberService {

    private final MemberRepository memberRepository; //DI

    /*
    @Autowired //생성자 인젝션, 테스트 코드 작성이나 실행시 변환 방지를 위해 @RequiredArgsConstructor이 final 필드를 보고 자동 생성 해줌
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    */

    //회원 가입
    @Transactional //따로 설정해서 기본을 따르게 함
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }
    //중복 회원 검증
    private void validateDuplicateMember(Member member) {
        //Exception
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
    //회원 전체 조회

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }
    //단건 조회
    public Member findOne(Long MemberId) {
        return memberRepository.findOne(MemberId);
    }
}
