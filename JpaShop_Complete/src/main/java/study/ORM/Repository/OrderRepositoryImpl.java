package study.ORM.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;
import study.ORM.Dto.Order.OrderDto;
import study.ORM.Dto.Order.OrderSearch;
import study.ORM.Dto.Order.QOrderDto;
import study.ORM.Dto.OrderItem.OrderItemDto;
import study.ORM.Dto.OrderItem.QOrderItemDto;
import study.ORM.Entity.Item.QItem;
import study.ORM.Entity.Order.Order;
import study.ORM.Entity.Order.QOrder;
import study.ORM.Entity.Order.QOrderItem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static study.ORM.Entity.Delivery.QDelivery.delivery;
import static study.ORM.Entity.Item.QItem.item;
import static study.ORM.Entity.Order.QOrder.order;
import static study.ORM.Entity.Order.QOrderItem.orderItem;
import static study.ORM.Entity.QMember.member;


public class OrderRepositoryImpl implements OrderRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    public OrderRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Order> findAllByDto_optimization(OrderSearch orderSearch) {
        BooleanExpression predicate = null;

        if (orderSearch.getOrderStatus() != null) {
            predicate = order.status.eq(orderSearch.getOrderStatus());
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            BooleanExpression namePredicate = member.name.like("%" + orderSearch.getMemberName() + "%");
            predicate = predicate != null ? predicate.and(namePredicate) : namePredicate;
        }

        return jpaQueryFactory
                .selectFrom(order)
                .join(order.member, member)
                .where(predicate)
                .fetch();
    }

    @Override
    public List<OrderDto> findAllByDto_optimization() {
        List<OrderDto> result = findOrder();
        List<Long> orderIds = result.stream().map(o -> o.getOrderId()).collect(Collectors.toList());

        List<OrderItemDto> orderItems = jpaQueryFactory
                .select(new QOrderItemDto(
                        orderItem.order.id.as("orderId"),
                        item.name.as("itemName"),
                        orderItem.orderPrice,
                        orderItem.count))
                .from(orderItem)
                .join(orderItem.item, item)
                .where(orderItem.order.id.in(orderIds))
                .fetch();

        Map<Long, List<OrderItemDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemDto -> orderItemDto.getOrderId()));
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return result;
    }

//ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

    private List<OrderDto> findOrder() {
        return jpaQueryFactory
                .select(new QOrderDto(
                        order.id.as("orderId"),
                        member.name.as("userName"),
                        order.orderDate,
                        order.status.as("orderStatus"),
                        delivery.address))
                .from(order)
                .join(order.member, member)
                .join(order.delivery, delivery)
                .fetch();
    }
    private List<OrderItemDto> findOrderItems(Long orderId) {
        return jpaQueryFactory
                .select(new QOrderItemDto(
                        orderItem.order.id.as("orderId"),
                        item.name.as("itemName"),
                        orderItem.orderPrice,
                        orderItem.count))
                .from(orderItem)
                .join(orderItem.item, item)
                .where(orderItem.order.id.eq(orderId))
                .fetch();
    }
}
