package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id@GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String name;

    @Embedded //내장 타입을 포함하고 있다는 뜻
    private Address address;

    @OneToMany(mappedBy = "member") //내가 member_id의 주인이 아니고 거울일 뿐이다를 명시해준다. 그렇지 않으면 Order과 Member 클래스 둘 다에서 PK가 수정되기 떄문에
    private List<Order> orders = new ArrayList<>(); //컬렉션을 바꾸지말고 써야지 다양한 문제 해결이 가능하다.


}
