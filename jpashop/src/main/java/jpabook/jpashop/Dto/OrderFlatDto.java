package jpabook.jpashop.Dto;

import jpabook.jpashop.Domain.Address;
import jpabook.jpashop.Domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data //V6
public class OrderFlatDto { //한 방 쿼리 Order와 OrderItem을 조인해서 한 번에 가져오는 방법
    //OrderQueryDto에서 가져옴
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    //OrderItemQueryDto에서 가져옴
    private String itemName;
    private int orderPrice;
    private int count;

    public OrderFlatDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
