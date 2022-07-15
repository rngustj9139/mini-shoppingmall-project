package jpabook.jpashop.repository.query;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository { // OrderApiController의 orderV4를 위해 사용

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();

        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery("select new jpabook.jpashop.repository.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                               " from Order o" +
                               " join o.member m" +
                               " join o.delivery d", OrderQueryDto.class).getResultList();
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select new jpabook.jpashop.repository.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                               " from OrderItem oi" +
                               " join oi.item i" +
                               " where oi.order.id = :orderId", OrderItemQueryDto.class)
                                .setParameter("orderId", orderId)
                                .getResultList();
    }

    public List<OrderQueryDto> findAllByDto_optimization() { // OrderApiController의 orderV5를 위해 사용된다. V4는 쿼리를 여러번 날렸지만 이건 orderItems를 쿼리한방에 가져온뒤 메모리에서 order에 맞는 orderItems를 매칭해준다. (쿼리가 2번만 나간다.)
        List<OrderQueryDto> result = findOrders();

        List<Long> orderIds = toOrderIds(result);

        Map<Long, List<OrderItemQueryDto>> orderItemsMap = findOrderItemsMap(orderIds);

        result.forEach(o -> o.setOrderItems(orderItemsMap.get(o.getOrderId())));

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemsMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery("select new jpabook.jpashop.repository.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                " from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemsMap = orderItems.stream() // List를 Map으로 바꾸기 (이때 id가 사용된다, 또 Map은 메모리 위에 올라간다.)
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
        return orderItemsMap;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
        return orderIds;
    }

    public List<OrderFlatDto> findAllByDto_flat() { // OrderApiController의 orderV6를 위해 사용된다. (쿼리 한방으로 다 조회한다, but 데이터 중복 발생 => 따로 처리해야함)
        return em.createQuery("select new" +
                                      " jpabook.jpashop.repository.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                                      " from Order o" +
                                      " join o.member m" +
                                      " join o.delivery d" +
                                      " join o.orderItems oi" +
                                      " join oi.item i", OrderFlatDto.class).getResultList();
    }

}
