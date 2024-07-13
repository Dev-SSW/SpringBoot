package Study.Spring_Login_JWT.Member;

import Study.Spring_Login_JWT.Member.DTO.JoinRequest;
import Study.Spring_Login_JWT.Member.DTO.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service @Transactional @RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //ID 중복 확인
    public boolean checkLoginIdDuplicate(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    //회원가입
    public void join(JoinRequest joinRequest) {
        memberRepository.save(joinRequest.toEntity());
    }

    // BCryptPasswordEncoder를 통해서 비밀번호 암호화 작업 추가한 회원가입 로직
    public void securityJoin(JoinRequest joinRequest) {
        if(memberRepository.existsByLoginId(joinRequest.getLoginId())) {
            return;
        }
        joinRequest.setPassword(bCryptPasswordEncoder.encode(joinRequest.getPassword())); //비밀번호 암호화
        memberRepository.save(joinRequest.toEntity());
    }

    //로그인
    public Member login(LoginRequest loginRequest) {
        Member findMember = memberRepository.findByLoginId(loginRequest.getLoginId());
        //ID가 존재하지 않으면
        if(findMember == null)
            return null;
        /*
        //password가 일치하지 않으면 (JWT에서는 비밀번호 비교는 필요없다) (토큰으로 인가)
        if (!findMember.getPassword().equals(loginRequest.getPassword()))
            return null;
        */

        return findMember;
    }

    //로그인 한 Id로 Member 반환 메서드
    public Member getLoginMemberById(Long memberId){
        if(memberRepository.findById(memberId).isEmpty())
            return null;
        Optional<Member> findMember = memberRepository.findById(memberId);
        return findMember.orElse(null);
    }

    //로그인 한 loginId로 Member 반환 메서드
    public Member getLoginMemberByLoginId(String loginId) {
        if(memberRepository.findByLoginId(loginId) == null)
            return null;
        return memberRepository.findByLoginId(loginId);
    }

}
