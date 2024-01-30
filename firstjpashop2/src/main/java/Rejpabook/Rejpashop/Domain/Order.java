package Rejpabook.Rejpashop.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity@Getter@Setter
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //지연 로딩 (프록시 조회를 위해)
    @JoinColumn(name = "member_id") //Member의 PK가 Order에게 있기 때문에 이곳에 컬럼을 추가
    private Member member;          //주문 회원

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL) //Order의 PK가 OrderItem에 있기 때문에 OrderItem에 정의된 order라는 Order 변수에 매핑
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY) //연관된 엔티티도 함께 영속화 시키고 싶을 때 Cascade 사용
    @JoinColumn(name = "delivery_id") //Delivery의 PK는 Order에게 있기 때문에 이곳에 컬럼을 추가
    private Delivery delivery;       //배송 정보

    private LocalDateTime orderDate; //주문 시간

    @Enumerated(EnumType.STRING) //enum 타입을 받아옴
    private OrderStatus status; //주문 상태 [ORDER, CANCEL]

    //=연관 관계 메서드=//
    public void setMember(Member member){ //order에서 member 설정
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){ //order에서 orderitem 설정
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) { //order에서 delivery 설정
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==// //order 하나를 만들면 회원,배달을 설정하고 orderitem들을 집어 넣음 (어떤 회원(+ 주소)이 한 주문에 여러 상품을 담음)
    public static Order createOrder(Member member, Delivery delivery,OrderItem... orderItems) { //OrderItem 여러개
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER); //order 객체의 상태를 넣어줌 //order 객체가 주문인지 주문취소인지
        order.setOrderDate(LocalDateTime.now()); //order 객체의 주문 시간을 넣어줌 //order 객체가 주문된 시간을 넣어줌
        return order;
    }

    //==비즈니스 로직==//
    //**주문 취소**//
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) { //배송완료된 order이라면
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL); //현재 order의 OrderStatus를 취소로 바꿈
        for (OrderItem orderItem : orderItems) { //현재 order에 포함된 모든 orderitem들을 취소로 바꿈
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    //**전체 주문 가격 조회**//
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
