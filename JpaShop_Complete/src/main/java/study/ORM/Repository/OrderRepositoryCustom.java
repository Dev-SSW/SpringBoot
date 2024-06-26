package study.ORM.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.ORM.Dto.Order.OrderDto;
import study.ORM.Dto.Order.OrderSearch;
import study.ORM.Entity.Order.Order;

import java.util.List;

public interface OrderRepositoryCustom {
    List<OrderDto> findAllByDto_optimization();
    List<Order> findAllByDto_optimization(OrderSearch orderSearch);
}
