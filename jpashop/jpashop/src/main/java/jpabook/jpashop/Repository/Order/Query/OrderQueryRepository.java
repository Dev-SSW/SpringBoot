package jpabook.jpashop.Repository.Order.Query;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.Dto.OrderFlatDto;
import jpabook.jpashop.Dto.OrderItemQueryDto;
import jpabook.jpashop.Dto.OrderQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    //따로 만들어진 쿼리 리포지토리는 화면에 fit한 쿼리들을 작성한다
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() { //V4
        List<OrderQueryDto> result = findOrders();
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
            });
        return result;
    }
    private List<OrderItemQueryDto> findOrderItems(Long orderId) { //V4
        return em.createQuery(
                "select new jpabook.jpashop.Dto.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId) //orderId파라미터를 orderId의 값으로 비교 되도록
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() { //V4, V5
        return em.createQuery(
                        "select new jpabook.jpashop.Dto.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d"
                        , OrderQueryDto.class)
                        .getResultList();
    }

    public List<OrderQueryDto> findAllByDto_optimization() { //V5
        List<OrderQueryDto> result = findOrders();
        List<Long> orderIds = result.stream().map(o -> o.getOrderId()).collect(Collectors.toList());

        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.Dto.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class) //in 절 추가
                .setParameter("orderIds", orderIds) //orderId파라미터를 orderId의 값으로 비교 되도록
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId())); //MAP을 통한 성능 향상

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    public List<OrderFlatDto> findAllByDto_flat() { //V6
        return em.createQuery(
                        "select new jpabook.jpashop.Dto.OrderFlatDto(" +
                                "o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d" +
                                " join o.orderItems oi" +
                                " join oi.item i"
                        , OrderFlatDto.class)
                        .getResultList();
    }
}
