package Rejpabook.Rejpashop.Service;

import Rejpabook.Rejpashop.Domain.Member;
import Rejpabook.Rejpashop.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor //final이 하나일 때 자동으로 생성자 만들어서 의존성 주입을 해준다 (필드 주입, 생성자 주입 방식 x)
@Transactional(readOnly = true) //변경은 트랜잭션 안에서 가능, 변경이 많지 않을 경우 readOnly로 성능 향상시키기가 가능
public class MemberService {
    /*
    @Autowired //필드 주입 방식
    MemberRepository memberRepository; //의존성 주입 (스프링 빈에 등록한다 (변경이 가능하므로))
    */
    /*
    private final MemberRepository memberRepository; //생성자 주입 방식
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    */
    private final MemberRepository memberRepository;

    //**회원가입**//
    @Transactional //변경
    public Long join(Member member) {
        validateDuplicateMember(member); //중복회원검증
        memberRepository.save(member);
        return member.getId();
    }
    //**중복회원검증**//
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName()); //이름을 통해 Member 내를 검사
        if(!findMembers.isEmpty()) { //값이 있다면 중복이 된 것, 값이 없었다면 중복이 되지 않은 것 (회원 정보가)
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //**전체회원조회**//
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }
    //**단일회원조회**//
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }


}
