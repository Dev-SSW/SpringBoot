package study.Spring_Login.Domain;

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

    private MemberRole role;
}
