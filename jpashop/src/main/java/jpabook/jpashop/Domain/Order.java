package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id") //실질적으로 member_id를 수정하는 것은 Order클래스이다.
    private Member member;

    @OneToMany(mappedBy = "order", cascade  = CascadeType.ALL) //cascade를 두면 OrderItems에 들어있는 모든 엔티티들을 전부 선택하게 해준다. 같이 지우고 같이 생성되고
    //내가 order_id의 주인이 아니고 거울일 뿐이다를 명시해준다. 여기서는 order_id 수정 불가능하도록 OrderItem에서 수정 가능
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL) //이걸 설정하면 각각 퍼시스트하지 않고, ORDER 클래스가 퍼시스트될 때 같이 퍼시스트 된다.
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문 상태 [Order, Cancel]

    //==연관관계 편의 메서드==// //위치는 실제로 컨트롤 하는 쪽에 메서드를 만들어주는게 좋다.
    public void setMember(Member member) { //양 방향 연관관계 작성 시 좀더 편리하게 만들어주기 위한 작업
        this.member = member;
        member.getOrders().add(this); //Order order = new Order()의 order이 this이다
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem); //this.member = member를 넣어주는 것처럼 orderItems에 orderItem을 넣어준다.
        orderItem.setOrder(this); //Order order = new Order()를 만들 필요가 없어진다 (후에 구현할  때)
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    /** 주문 취소 **/
    public void cancle() {
        if(delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCLE);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancle();
        }
    }

    //==조회 로직==//
    /** 전체 주문 가격 조회 **/
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
        //return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum(); //같은 로직
    }
}
