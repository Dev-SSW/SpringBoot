package Study.Spring_Login_JWT.Member;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String loginId;
    private String password;
    private String name;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    /* 구글 로그인을 위한 변수 */
    /*
    // provider : google이 들어감
    private String provider;
    // providerId : 구굴 로그인 한 유저의 고유 ID가 들어감
    private String providerId;
    */

}
