package study.ORM.Entity.Delivery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import study.ORM.Entity.Address;
import study.ORM.Entity.Order.Order;

@Entity @Getter @Setter
public class Delivery {
    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @JsonIgnore                            //필요한 정보만 노출시키기 위해
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;               //각 주문마다 하나씩 주소지가 있다

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;         // 배송 상태에는 [READY, COMP]이 있다

}
