package jpabook.jpashop.QueryService.Order;

import jpabook.jpashop.Domain.Order;
import jpabook.jpashop.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderQueryService { //OSIV = false에 의해 트랜잭션 생명 주기 안으로 집어넣기 위해 API를 이곳에서 설정한다
    private final OrderRepository orderRepository;

    public List<OrderDto> orderV3() { //Order과 OrderItem에 대한 각 Order에 따라 4개가 나오게 된다 (같은 ID에 아이템 두개씩)
        //distinct를 이용해서 같은 id의 중복을 제거할 수 있다. 하지만 DB 쿼리에서는 모든 줄이 똑같은 것이 아니므로 그대로 4개이다
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(toList());
        return result;
    }
}
