package Study.Spring_Login_JWT.Security;

import Study.Spring_Login_JWT.Member.Member;
import Study.Spring_Login_JWT.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//UserDetails로 사용자 정보를 보냄
@Service @RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(username);
        if(member != null) {
            return new CustomSecurityUserDetails(member);
        }
        return null;
    }
}
