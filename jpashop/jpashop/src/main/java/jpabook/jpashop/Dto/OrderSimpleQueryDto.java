package jpabook.jpashop.Dto;

import jpabook.jpashop.Domain.Address;
import jpabook.jpashop.Domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data //DTO로 받아온다 (따로 DTO 파일을 만든 이유는 리포지토리가 컨트롤러와의 의존관계가 생기는 오류가 생길 수 있어서)
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}
