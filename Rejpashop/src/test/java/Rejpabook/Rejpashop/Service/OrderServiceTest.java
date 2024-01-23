package Rejpabook.Rejpashop.Service;

import Rejpabook.Rejpashop.Domain.Address;
import Rejpabook.Rejpashop.Domain.Item.Book;
import Rejpabook.Rejpashop.Domain.Item.Item;
import Rejpabook.Rejpashop.Domain.Member;
import Rejpabook.Rejpashop.Domain.Order;
import Rejpabook.Rejpashop.Domain.OrderStatus;
import Rejpabook.Rejpashop.Exception.NotEnoughStockException;
import Rejpabook.Rejpashop.Repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class OrderServiceTest {
    @PersistenceContext
    EntityManager em;

    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);
        Assertions.assertEquals(OrderStatus.ORDER, getOrder.getStatus(),"상품 주문시 상태는 ORDER");
        Assertions.assertEquals(1, getOrder.getOrderItems().size(),"주문한 상품 종류 수가 정확해야 한다.");
        //상품의 개수는 한 개
        Assertions.assertEquals(10000*orderCount, getOrder.getTotalPrice(),"주문 가격은 가격 * 수량이다.");
        //상품 가격은 10000*2
        Assertions.assertEquals(8, item.getStockQuantity(),"주문 수량만큼 재고가 줄어야 한다.");
        //주문 수량이 2이므로 남은 재고는 8이어야 한다

    }
    @Test
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        int orderCount = 11;
        //then
        Assertions.assertThrows(NotEnoughStockException.class, () -> {orderService.order(member.getId(), item.getId(), orderCount);});
    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        Long orderId = orderService.order(member.getId(), item.getId(), 8);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);
        Assertions.assertEquals(10, item.getStockQuantity(),"주문 취소만큼 재고가 늘어야 한다.");
        Assertions.assertEquals(OrderStatus.CANCEL, getOrder.getStatus(),"상품 취소 시 상태는 CANCEL");
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return  member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }
}
