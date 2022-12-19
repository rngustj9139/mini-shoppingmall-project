package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.query.OrderFlatDto;
import jpabook.jpashop.repository.query.OrderItemQueryDto;
import jpabook.jpashop.repository.query.OrderQueryDto;
import jpabook.jpashop.repository.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;

@RestController
@RequiredArgsConstructor
public class OrderApiController { // OrderSimpleApiController는 ManyToOne이나 OneToOne이었던것을 다뤘지만 이것은 OneToMany같은 것을 다룬다. (컬렉션 조회)

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() { // 엔티티를 직접노춣
        List<Order> all = orderRepository.findAll(new OrderSearch());

        for (Order order : all) { // LAZY 로딩이므로 JPA를 이용해 터치(강제 초기화)를 해주어야한다. => 가짜 프록시 객체를 이용하는 것이 아닌 DB에서 가져와야한다.
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();

            orderItems.stream()
                    .forEach(o -> o.getItem().getName());
        }

        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return collect;
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3() { // 페치 조인 적용
        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return collect;
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> orderV3_1(@RequestParam(value = "offset", defaultValue = "0") int offset, // application.yml의 default_batch_fetch_size 확인
                                    @RequestParam(value = "limit", defaultValue = "100") int limit) { // 페이징 수행
        List<Order> orders = orderRepository.findAllWithMemberDelivery2(offset, limit); // OneToOne, ManyToOne은 그냥 페치 조인을 하고 ToMany(컬렉션 조회)는 페치 조인시 데이터 뻥튀기 문제가 발생한다. => default_batch_fetch_size 이용해야한다.

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return collect;
    }

    @GetMapping("/api/v4/orders") // 컬렉션 조회시 1 + N 문제 존재(OrderQueryRepository 참고) (DTO 직접 조회)
    public List<OrderQueryDto> orderV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5() { // 쿼리가 1 + 1개만 나간다. (DTO 직접 조회) (1 + N 문제 해결)
        return orderQueryRepository.findAllByDto_optimization();
    }

    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> orderV6() { // (DTO 직접 조회) orderV6는 쿼리가 1개만 나간다. 상황에 따라 orderV6가 orderV5보다 느릴 수 있다. (쿼리는 한번이지만 데이터 중복이 발생하고 메모리 영역에서 분할을 해주어야한다. - 중복을 없에야한다. 페이징도 불가능)
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        return flats.stream() // 데이터 중복 방지(뻥튀기가 발생하므로), grouping을 사용하려면 OrderQueryDto에 @EqualsAndHashCode 어노테이션을 추가해야함
                .collect(Collectors.groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        Collectors.mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), Collectors.toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Data
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems; // DTO안에 엔티티가 있으면 안된다!!!!!!!!!! OrderItem 조차도 DTO로 변경해야한다.

        public OrderDto(Order o) { // 생성자
            orderId = o.getId();
            name = o.getMember().getName();
            orderDate = o.getOrderDate();
            orderStatus = o.getStatus();
            address = o.getDelivery().getAddress();

            // DTO안에 엔티티가 있으면 안된다!!!!!!!!!! OrderItem 조차도 DTO로 변경해야한다.
            orderItems = o.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList()); // Order에서 OrderItem은 OneToMany이고 디폴트가 LAZY로딩이므로 강제 초기화를 수행해야한다.
        }

    }

    @Data
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }

    }

}
