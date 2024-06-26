package study.ORM.Dto.OrderItem;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * study.ORM.Dto.OrderItem.QOrderItemDto is a Querydsl Projection type for OrderItemDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QOrderItemDto extends ConstructorExpression<OrderItemDto> {

    private static final long serialVersionUID = -362511453L;

    public QOrderItemDto(com.querydsl.core.types.Expression<Long> orderId, com.querydsl.core.types.Expression<String> itemName, com.querydsl.core.types.Expression<Integer> orderPrice, com.querydsl.core.types.Expression<Integer> count) {
        super(OrderItemDto.class, new Class<?>[]{long.class, String.class, int.class, int.class}, orderId, itemName, orderPrice, count);
    }

}

