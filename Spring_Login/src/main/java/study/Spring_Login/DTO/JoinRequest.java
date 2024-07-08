package study.Spring_Login.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import study.Spring_Login.Domain.Member;
import study.Spring_Login.Domain.MemberRole;

//회원가입 시 데이터를 받는 폼
@Getter @Setter @NoArgsConstructor
public class JoinRequest {
    @NotBlank(message = "ID를 입력하세요.") // 빈칸 허용하지 않음, 빈칸일 때 에러 메세지 출력
    private String loginId;

    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password;
    private String passwordCheck;

    @NotBlank(message = "이름을 입력하세요.")
    private String name;

    public Member toEntity() {
        return Member.builder()
                .loginId(this.loginId)
                .password(this.password)
                .name(this.name)
                .role(MemberRole.ADMIN)
                .build();
    }
}
