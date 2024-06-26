package study.ORM.Dto.Order;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * study.ORM.Dto.Order.QOrderDto is a Querydsl Projection type for OrderDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QOrderDto extends ConstructorExpression<OrderDto> {

    private static final long serialVersionUID = 1531040265L;

    public QOrderDto(com.querydsl.core.types.Expression<Long> orderId, com.querydsl.core.types.Expression<String> userName, com.querydsl.core.types.Expression<java.time.LocalDateTime> orderDate, com.querydsl.core.types.Expression<study.ORM.Entity.Order.OrderStatus> orderStatus, com.querydsl.core.types.Expression<? extends study.ORM.Entity.Address> address) {
        super(OrderDto.class, new Class<?>[]{long.class, String.class, java.time.LocalDateTime.class, study.ORM.Entity.Order.OrderStatus.class, study.ORM.Entity.Address.class}, orderId, userName, orderDate, orderStatus, address);
    }

}

