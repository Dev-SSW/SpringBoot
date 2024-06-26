package study.ORM.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import study.ORM.Entity.Item.Item;
import study.ORM.Entity.Order.Order;

public interface OrderRepository extends JpaRepository<Order, Long>,OrderRepositoryCustom {
    Order findOrderById(Long id); //단건 조회용
}
