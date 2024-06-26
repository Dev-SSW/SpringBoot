package study.ORM.Entity.Order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import study.ORM.Entity.Delivery.Delivery;
import study.ORM.Entity.Delivery.DeliveryStatus;
import study.ORM.Entity.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter
@Table(name = "ORDERS")
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")                 //Member의 PK의 주인은 Order이다
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) //엔티티에 관련된 모든 엔티티를 함께 영속화 시킨다 (OrderItem)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade =  CascadeType.ALL)           //엔티티에 관련된 모든 엔티티를 함께 영속화 시킨다 (Delivery)
    @JoinColumn(name = "delivery_id")               //Delivery의 PK의 주인은 Order이다
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;                     //주문 상태에는 [ORDER, CANCEL]이 있다.

    //==연관관계 편의 메서드==//
    //Member와의 연관관계 편의 메서드 (다 쪽에 위치)
    public void setMember(Member member) {          //PK가 Order에 있기에 member가 Order을 찾아갈 수 없으므로 연관관계 설정
        this.member = member;
        member.getOrders().add(this);
    }
    //OrderItem과의 연관관계 편의 메서드 (일 쪽에 위치)
    public void addOrderItem(OrderItem orderItem) { //PK가 OrderItem에 있기에 Order가 OrderItem을 찾아갈 수 없으므로 연관관계 설정
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    //Delivery와의 연관관계 편의 메서드 (일대일)
    public void setDelivery(Delivery delivery) {    //PK가 Order에 있기에 Delivery가 Order을 찾아갈 수 없으므로 연관관계 설정
        this.delivery = delivery;
        delivery.setOrder(this);                    //delivery가 .setDelivery를 사용할 때 Order을 찾아갈 수 있도록
    }

    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);            //주문의 주문자를 주입
        order.setDelivery(delivery);        //주문의 배송지를 주입
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);  //주문에 주문 상품들을 주입
        }
        order.setStatus(OrderStatus.ORDER); //주문의 상태를 주입
        order.setOrderDate(LocalDateTime.now()); //주문 날짜를 주입
        return order;
    }

    //==비즈니스 로직==//
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) { //주문의 배송 상태를 취소로 만듦
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();             //주문에 담긴 주문 상품들 모두 cancel 함수로 취소
        };
    }

    //==조회 로직==//
    /** 전체 주문 가격 조회 */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice(); //주문에 담긴 주문 상품들의 가격을 모두 더해서 totalPrice 만듦
        }
        return totalPrice;
    }
}
