package study.ORM.Controller.Api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.ORM.Dto.Order.OrderDto;
import study.ORM.Repository.OrderRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;

    //JPA에서 DTO로 직접 조회 - 컬렉션 조회 최적화
    @GetMapping("api/orders/V1")
    public List<OrderDto> searchOrderV1() {
        return orderRepository.findAllByDto_optimization();
    }
}
