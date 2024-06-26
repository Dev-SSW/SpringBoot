package study.ORM;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import study.ORM.Entity.Address;
import study.ORM.Entity.Delivery.Delivery;
import study.ORM.Entity.Item.Book;
import study.ORM.Entity.Member;
import study.ORM.Entity.Order.Order;
import study.ORM.Entity.Order.OrderItem;

//** userA는 JPA BOOK 1, 2를 주문, userB는 SPRING BOOK 1, 2를 주문 (DB에 삽입할 데이터)
@Component //스프링이 컴포넌트의 대상으로 인식한다
@RequiredArgsConstructor
public class initDb {

    private final InitService initService;
    @PostConstruct //스프링이 호출을 해준다
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }
    @Component @Transactional @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;
        public void dbInit1() {
            Member member = createMember("userA","서울","1","1111");
            em.persist(member);

            Book book1 = createBook("JPA BOOK1", 10000, 100);
            em.persist(book1);
            Book book2 = createBook("JPA BOOK2",20000,100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public void dbInit2() {
            Member member = createMember("userB","진주", "2", "2222");
            em.persist(member);

            Book book1 = createBook("SPRING BOOK1",20000, 200 );
            em.persist(book1);
            Book book2 = createBook("SPRING BOOK2",40000, 300 );
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private static Member createMember(String user, String city, String s, String z) {
            Member member = new Member();
            member.setName(user);
            member.setAddress(new Address(city, s, z));
            return member;
        }

        private static Book createBook(String BookName,int price, int stock) {
            Book book1 = new Book();
            book1.setName(BookName);
            book1.setPrice(price);
            book1.setStockQuantity(stock);
            return book1;
        }

        private static Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }
    }

}

