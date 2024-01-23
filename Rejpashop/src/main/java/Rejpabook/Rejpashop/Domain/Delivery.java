package Rejpabook.Rejpashop.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity@Getter@Setter
public class Delivery {
    @Id@GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    //Delivery의 PK가 Order에게 있기 때문에 Order에 정의된 delivery라는 Delivery 변수에 매핑
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; //Enum [READY, COMP]

}

