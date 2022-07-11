package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController { // OrderSimpleApiController는 ManyToOne이나 OneToOne이었던것을 다뤘지만 이것은 OneToMany같은 것을 다룬다. (컬렉션 조회)

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());

        for (Order order : all) { // LAZY 로딩이므로 JPA를 이용해 터치(강제 초기화)를 해주어야한다. => 가짜 프록시 객체를 이용하는 것이 아닌 DB에서 가져와야한다.
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();

            orderItems.stream().forEach(o -> o.getItem().getName());
        }

        return all;
    }

}
