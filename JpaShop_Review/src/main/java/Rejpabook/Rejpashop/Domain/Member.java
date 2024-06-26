package Rejpabook.Rejpashop.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String name;
    @Embedded //Embeddable 타입을 받아옴
    private Address address;

    @OneToMany(mappedBy = "member") //Member의 PK가 Order에게 있기 때문에 Order에 정의된 member라는 Member 변수에 매핑
    private List<Order> orders = new ArrayList<>();
}
