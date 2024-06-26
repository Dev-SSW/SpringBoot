package study.ORM.Dto.Order;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import study.ORM.Dto.OrderItem.OrderItemDto;
import study.ORM.Entity.Address;
import study.ORM.Entity.Order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long orderId;
    private String userName;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItems;

    @QueryProjection
    public OrderDto(Long orderId, String userName, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.userName = userName;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }


}
