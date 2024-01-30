package jpabook.jpashop.QueryService.Order;

import jpabook.jpashop.Domain.Address;
import jpabook.jpashop.Domain.Order;
import jpabook.jpashop.Domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class OrderDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    private List<OrderItemDto> orderItems;

    public OrderDto(Order order) {
        orderId = order.getId();
        name = order.getMember().getName();
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress();
        orderItems = order.getOrderItems().stream().map(orderItem -> new OrderItemDto(orderItem)).collect(toList());
    }
}
