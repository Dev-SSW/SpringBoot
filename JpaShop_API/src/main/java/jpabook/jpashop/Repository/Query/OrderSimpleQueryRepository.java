package jpabook.jpashop.Repository.Query;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.Dto.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    //따로 만들어진 쿼리 리포지토리는 화면에 fit한 쿼리들을 작성한다
    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDto() { // V4/simple-orders
        return em.createQuery(
                "select new jpabook.jpashop.Dto.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o"+
                        " join o.member m"+
                        " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
