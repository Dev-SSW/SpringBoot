package study.ORM.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.ORM.Entity.Member;
import study.ORM.Repository.MemberRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    /** 중복회원검증 */
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByname(member.getName()); //Member 내에 같은 이름이 있는 지를 검색
        if(!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /** 회원가입 */
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    /** 전체회원조회 */
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    /** 단일회원조회 */
    public Member findOne(Long memberId) {
        return memberRepository.findMemberById(memberId);
    }

    @Transactional //Api에서 수정을 위해 만든 함수 (변경 감지)
    public void update(Long id,String username) {
        Member member = memberRepository.findMemberById(id);
        member.setName(username);
    }
}
