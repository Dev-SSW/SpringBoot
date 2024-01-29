package jpabook.jpashop.Api;

import jpabook.jpashop.Domain.Address;
import jpabook.jpashop.Domain.Order;
import jpabook.jpashop.Domain.OrderStatus;

import jpabook.jpashop.Dto.OrderSimpleQueryDto;

import jpabook.jpashop.Repository.Order.Simplequery.OrderSimpleQueryRepository;
import jpabook.jpashop.Repository.OrderRepository;
import jpabook.jpashop.Repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

//지연 로딩 성능 문제를 해결하기 위한 방법들
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("api/v1/simple-orders")
    //엔티티를 직접 노출할 때는 양방향 연관 관계 중 한 곳에 꼭 @jsonignore를 적용해야 한다 (그렇지 않으면 무한 루프 오류 발생!)
    //order->membe와 order->address는 지연 로딩이다. 따라서 실제 엔티티 대신 프록시가 존재
    //jackson 라이브러리는 이 프록시를 관리하지 못해서 (알아보지 못함) 오류 발생! ->
    // 하이버네이트 모듈을 적용해야 한다
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //LAZY 강제 초기화 order.getMember까지는 지연로딩, getName에서부터는 DB에서 끌어옴
            order.getDelivery().getAddress(); //LAZY 강제 초기화
            //초기화된 지연 로딩만 출력되게 된다
        }
        return all;
    }
    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    @GetMapping("api/v2/simple-orders") //쿼리가 너무 많이 나간다는 단점이 있다 (성능이 좋지 않다)
    public List<SimpleOrderDto> ordersV2() {
        //Order 2개인 것을 조회 //N+1문제 (1은 첫번째 쿼리를 말함, 첫번째 쿼리로 N개를 가져오고 N개 안에는 여러 N개가 또 있다 (회원 N, 배송 N)
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        //각각의 Order에서 멤버와 딜리버리를 한번 씩 조회, -> 총 5번 쿼리가 나감
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))//A->B로 바꾸는 order->simpleorderdto로
                .collect(Collectors.toList());
        return result;
    }
    @Data //dto로 받아온다
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화 //영속성 컨텍스트에 없으므로 DB를 조회해야해서 쿼리를 날림
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();; //LAZY 초기화
            // 만약 동일 사용자라면 name은 똑같기 때문에 영속성 컨텍스트에서 끌어오고, address만 DB에서 조회한다
            //-> 쿼리가 5번에서 4번으로 줄어들긴 한다 (하이버네이트 6 오류로 쿼리가 7번 나가고 있긴 하다)
        }
    }

    @GetMapping("api/v3/simple-orders") //쿼리가 한 번 나간다 (JPQL의 petch join을 사용하는 방법)
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    @GetMapping("api/v4/simple-orders") //내가 원하는 것만 셀렉트 구문을 보내는 것이 V3와 차이점 (직접 쿼리를 넣었기 때문에)
    //V3는 셀렉트 절이 매우 길지만, V4는 셀렉트 절이 원하는 것만 담아오기에 짧다
    //fit하게 만들어왔기에 효율적이지만 V3처럼의 유연성이 없기에 서로 장단점이 있다
    //리포지토리의 재사용성이 떨어짐
    //사실 V3와 성능 차이가 그렇게 크지 않다
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDto();
    }

}
