package study.ORM.Entity.Order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import study.ORM.Entity.Item.Item;

@Entity @Getter @Setter
@Table(name = "order_item")
public class OrderItem {
    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int orderPrice;
    private int count;

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);                //주문 상품의 아이템을 주입
        orderItem.setOrderPrice(orderPrice);    //주문 상품의 주문 가격을 주입
        orderItem.setCount(count);              //주문 상품의 수량을 주입

        item.removeStock(count);                //주문 수량만큼 재고를 감소
        return orderItem;
    }

    //==비즈니스 로직==//
    /** 주문 취소 */
    public void cancel() { getItem().addStock(count); } //취소한 주문 수량만큼 재고를 증가

    //==조회 로직==//
    /** 주문 상품 전체 가격 조회 */
    public int getTotalPrice() { return getOrderPrice() * getCount(); }
}
