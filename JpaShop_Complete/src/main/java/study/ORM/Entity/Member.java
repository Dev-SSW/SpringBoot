package study.ORM.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

import lombok.Setter;
import study.ORM.Entity.Order.Order;

import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @JsonIgnore //필요한 정보만 노출 시키기 위해
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    @Embedded
    private Address address; //고객의 주소지

    private String name;

}
