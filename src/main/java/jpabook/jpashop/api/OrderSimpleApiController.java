package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.OrderSimpleQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() { // 이 함수는 필요 없는 데이터까지 노출되서 문제가 생긴다. (엔티티를 직접 노출)
        List<Order> all = orderRepository.findAll(new OrderSearch());

        for (Order order : all) {
            order.getMember().getName(); // LAZY 로딩이지만 직접 DB에 접근해서 데이터를 가져온다.(강제 초기화) => 가짜 프록시 객체 대신 사용한다.
            order.getDelivery().getAddress();
        }

        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2() { // 원래 DTO들의 묶음을 List로 반환하지 않고 한번 감싸서 반환해야함, v1과 v2 모두 LAZY 로딩시 쿼리가 너무 많이 나간다는 단점이 존재한다.
        List<Order> orders = orderRepository.findAll(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3() { // 이건 V2의 1 + N 문제를 해결해준다 (페치 조인 사용)
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v4/simple-orders") // v3와 v4모두 패치조인되는 것은 똑같다.
    public List<OrderSimpleQueryDto> orderV4() { // orderV3와는 달리 jpa가 쿼리를 송신할 때 select절에서 원하는 것만 select한다. (select절의 양이 줄어들게 된다. => 네트워크 자원의 사용을 줄일 수 있다. v4는 v3보다 성능 최적화가 되는 장점이 있찌만 코드가 살짝 지저분해지는 단점이 존재한다. => v3와 v4의 우열을 가리기 힘들다.)
        return orderRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) { // 생성자
            orderId = order.getId(); // LAZY 강제 초기화
            name = order.getMember().getName(); // LAZY 강제 초기화
            orderDate = order.getOrderDate(); // LAZY 강제 초기화
            orderStatus = order.getStatus(); // LAZY 강제 초기화
            address = order.getDelivery().getAddress(); // LAZY 강제 초기화

        }

    }

}
