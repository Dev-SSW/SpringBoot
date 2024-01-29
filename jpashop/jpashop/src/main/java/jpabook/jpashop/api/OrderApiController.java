package jpabook.jpashop.Api;

import jpabook.jpashop.Domain.Address;
import jpabook.jpashop.Domain.Order;
import jpabook.jpashop.Domain.OrderItem;
import jpabook.jpashop.Domain.OrderStatus;
import jpabook.jpashop.Dto.OrderFlatDto;
import jpabook.jpashop.Dto.OrderItemQueryDto;
import jpabook.jpashop.Dto.OrderQueryDto;
import jpabook.jpashop.Repository.Order.Query.OrderQueryRepository;
import jpabook.jpashop.Repository.OrderRepository;
import jpabook.jpashop.Repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    //OrderItem의 일대다 조인의 문제로 데이터 베이스 쿼리가 증가하기에 성능이 떨어지는 것을 해결하기 위한 방법들

    @GetMapping("api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems(); //LAZY 강제 초기화
            /*
            for (OrderItem orderItem : orderItems) {
                orderItem.getItem().getName();
            }
            */
            orderItems.stream().forEach(o -> o.getItem().getName()); //foreach 반복문
        }
        return all;
    }
    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    @GetMapping("api/v2/orders")
    public List<OrderDto> orderV2() {
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o)).collect(toList());
        return collect;
    }

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; //Address의 경우 value 엔티티이기 때문에 노출해도 괜찮다
        //private List<OrderItem> orderItems; //DTO에 엔티티가 존재해서는 안되는데 orderItem은 엔티티이다
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream().map(orderItem -> new OrderItemDto(orderItem)).collect(toList());

            /*
            order.getOrderItems().stream().forEach(o -> o.getItem().getName());
            orderItems = order.getOrderItems(); //orderItem의 엔티티의 모든 내용이 노출될 수 있으므로 DTO를 하나 더 만든다
            */
        }
    }

    @Data
    static class OrderItemDto {
        private String itemName; //상품명
        private int orderPrice; //주문가격
        private int count; //주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    @GetMapping("api/v3/orders")
    //쿼리가 한 번으로 나간다 (fetch join 이용) but. fetch join을 일대다에서 사용하면 페이징이 불가능하다
    //페이징 : 몇 번째부터 몇 번째까지의 리스트만 가져오는 것
    //컬렉션 페치 조인은 하나만 써야한다
    public List<OrderDto> orderV3() { //Order과 OrderItem에 대한 각 Order에 따라 4개가 나오게 된다 (같은 ID에 아이템 두개씩)
        //distinct를 이용해서 같은 id의 중복을 제거할 수 있다. 하지만 DB 쿼리에서는 모든 줄이 똑같은 것이 아니므로 그대로 4개이다
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(toList());
        return result;
    }
    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

    @GetMapping("api/v3.1/orders") //yml에서 배치 사이즈를 설정했다 (물품이 10개일 때 배치 사이즈를 2로하면 쿼리는 5번만 나간다, (미리 끌어온다))
    public List<OrderDto> orderV3_page( //V3는 쿼리는 하나가 나가지만, 데이터 전송이 4개이다
                                        //V3.1은 쿼리가 세 개 나가지만, 페이징이 가능하고, 데이터 전송은 2개이다 (많은 물품이 있거나, 사용자가 많을 때 더욱 효율적이다)
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "offset", defaultValue = "100") int limit)
    {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit); //OrderItem을 가져오지 않는 함수
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(toList());
        return result;
    }

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

    @GetMapping("api/v4/orders") //ToOne 관계를 먼저 조회하고 ToMany는 별도의 DTO를 하나 더 만들어서 최적화를 따로 시켜줌
                                 //DTO를 별도로 만들어서 적용해줬다 (컨트롤러가 리포지토리와 연관관계가 설정될 수 있으므로)
                                 //JPA에서 DTO를 직접 조회하는 방법 (fit하게 가져온다!), 컬렉션 조회 포함
                                 //엔티티에서 DTO를 만들어서 하는 V3 이전 방법보다 조금 더 성능 최적화가 가능하다
    public List<OrderQueryDto> orderV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

    @GetMapping("api/v5/orders")
    public List<OrderQueryDto> orderV5() { //쿼리 2번으로 최적화 (트레이드 오프 관계이다)
                                           //코드 작성이 join fetch에 비해 복잡하나 셀렉트 문이 줄어들어 성능이 향상된다
        return orderQueryRepository.findAllByDto_optimization();
    }

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

    @GetMapping("api/v6/orders")
    public List<OrderQueryDto> orderV6() { //쿼리 1번으로 최적화 //데이터 중복이 되므로 V5보다 성능 최적화가 되지 않을 수도 있다
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(
                                o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress())
                        , mapping(o -> new OrderItemQueryDto(
                                o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())))
                .entrySet().stream().map(e -> new OrderQueryDto(
                        e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(),
                        e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());

    }
}
