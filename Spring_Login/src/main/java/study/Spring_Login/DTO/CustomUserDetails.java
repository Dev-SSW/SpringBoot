package study.Spring_Login.DTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import study.Spring_Login.Domain.Member;

import java.util.ArrayList;
import java.util.Collection;
public class CustomUserDetails implements UserDetails {
    private final Member member;
    public CustomUserDetails(Member member) {
        this.member = member;
    }
    // 현재 member의 role을 반환 (ex. "ROLE_ADMIN" / "ROLE_USER" 등)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "ROLE_" + member.getRole().name();
            }
        });
        return collection;
    }
    // user의 비밀번호 반환
    @Override
    public String getPassword() {
        return member.getPassword();
    }
    // user의 LoginId를 반환
    @Override
    public String getUsername() {
        return member.getLoginId();
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
    //isAccountNonExpired() : 계정 만료 여부 => true : 만료 X
    //isAccountNonLocked() : 계정 잠김 여부 => true : 잠김 X
    //isCredentialsNonExpired() : 비밀번호 만료 여부 => true : 만료 X
    //isEnabled() : 계정 사용 가능 여부 => true : 사용 가능 O
}
