package jpabook.jpashop.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    // @NotEmpty //값이 없으면 send 할 수 없도록 함 (postman) , but DTO에 넣는것이 개발자 입장에서는 어떤 값이 notempty인지 알 수 있으므로 DTO에 포함 시킨다
    private String name;
    @Embedded //Embeddable 타입을 받아옴
    private Address address;

    //@JsonIgnore //필요한 정보만 노출시키고 싶으면 (엔티티를 외부에 노출시키지 않기 위해)
    @OneToMany(mappedBy = "member") //Member의 PK가 Order에게 있기 때문에 Order에 정의된 member라는 Member 변수에 매핑
    private List<Order> orders = new ArrayList<>();
}
