package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //protected OrderItem() {} 의 의미를 가진다.
public class OrderItem {
    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch =  LAZY) //실무에서 LAZY를 사용해야한다. ENGER일 경우 모든 쿼리르 n+1번 가져와 성능이 느려진다. @ManyToOne에서는 기본이 앵거
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id") //실질적으로 order_id를 수정하는 것은 OrderItem 클래스이다.
    private Order order;

    private int orderPrice; //주문 가격
    private int count; //주문 수량

    //protected OrderItem() {} //createOrderItem 함수로 생성하는게 아니라 다른 사람이 코드마다 직접적으로 생성할 수 있으므로 그것을 막기 위한 방법

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);
        item.removeStock(count);
        return orderItem;
    }
    //==비지니스 로직==//
    public void cancle() {
        getItem().addStock(count);
    }
    //==조회 로직==//
    /** 주문 상품 전체 가격 조회 **/
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
