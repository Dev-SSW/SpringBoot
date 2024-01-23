package Rejpabook.Rejpashop.Domain;

import Rejpabook.Rejpashop.Domain.Item.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity@Getter@Setter
@Table(name = "order_item")
public class OrderItem {
    @Id@GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id") //Item의 PK는 OrderItem에게 있기 때문에 이곳에 컬럼을 추가
    private Item item;      //주문 상품

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") //Order의 PK는 OrderItem에게 있기 때문에 이곳에 컬럼을 추가
    private Order order;    //주문

    private int orderPrice; //주문 가격
    private int count;      //주문 수량

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count); //주문 수량만큼 재고를 감소
        return orderItem;
    }

    //==비즈니스 로직==//
    //**주문 취소**//
    public void cancel() {
        getItem().addStock(count); //item을 가져와서 취소한 주문 수량만큼 재고를 증가
    }

    //==조회 로직==//
    //**주문 상품 전체 가격 조회**//
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
