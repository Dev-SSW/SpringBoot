package jpabook.jpashop.QueryService.Order;

import jpabook.jpashop.Domain.OrderItem;
import lombok.Data;

@Data
public class OrderItemDto {
    private String itemName; //상품명
    private int orderPrice; //주문가격
    private int count; //주문 수량

    public OrderItemDto(OrderItem orderItem) {
        itemName = orderItem.getItem().getName();
        orderPrice = orderItem.getOrderPrice();
        count = orderItem.getCount();
    }
}
