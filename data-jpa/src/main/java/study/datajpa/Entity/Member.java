package study.datajpa.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //protected Member() { } 를 쓰지 않아도 보호 수준을 맞춰 주는 Annotation
@ToString(of = {"id", "username", "age"}) //연관 관계를 포함한 변수는 제외 하여야 한다
@NamedQuery( //실무에서 사용 하지 않는 방법
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username"
)
//네임드로 엔티티그래프 사용
@NamedEntityGraph(name= "Member.all", attributeNodes = @NamedAttributeNode("team"))
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY) //프록시 이용
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    public void changeTeam(Team team) { //팀을 바꾸면 반대쪽 연관 관계의 팀도 바꿔 줘야 한다
        this.team = team; //Member의 Team을 team으로 설정
        team.getMembers().add(this); //Team의 Member에 이 Member를 추가
    }
}
