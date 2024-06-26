package study.ORM.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.ORM.Dto.Order.OrderDto;
import study.ORM.Dto.Order.OrderSearch;
import study.ORM.Entity.Delivery.Delivery;
import study.ORM.Entity.Delivery.DeliveryStatus;
import study.ORM.Entity.Item.Item;
import study.ORM.Entity.Member;
import study.ORM.Entity.Order.Order;
import study.ORM.Entity.Order.OrderItem;
import study.ORM.Repository.ItemRepository;
import study.ORM.Repository.MemberRepository;
import study.ORM.Repository.OrderRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    /** 주문 */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        //엔티티 조회
        Member member = memberRepository.findMemberById(memberId);
        Item item = itemRepository.findItemById(itemId);

        //배송 정보 설정
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        //주문 상품 설정
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        return order.getId();
    }

    /** 주문 취소 */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOrderById(orderId);

        //주문 취소
        order.cancel();
    }

    /** 주문 검색 */
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByDto_optimization(orderSearch);
    }
}
