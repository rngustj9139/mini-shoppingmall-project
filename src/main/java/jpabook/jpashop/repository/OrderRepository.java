package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

//    public List<Order> findAll(OrderSearch orderSearch) { // queryDSL 없이 검색 구현 => but 멤버 이름과 주문 상태가 없을 때에는 모든 것을 조회해야함 => 동적 쿼리가 필요함, 이코드는 특정회원의 주문은 검색할 수 있지만 회원명이 없을때 전체조회는 안된다.
//        return em.createQuery("select o from Order o join o.member m" + // jpql에서의 조인 방법임
//                " where o.status = :status" +
//                " and m.name like :name", Order.class)
//                .setParameter("status", orderSearch.getOrderStatus())
//                .setParameter("name", orderSearch.getMemberName())
//                .setMaxResults(1000) // 페이징 할때 가져울 개수 (최대 1000건)
//                .getResultList();
//    }

    public List<Order> findAll(OrderSearch orderSearch) { // queryDSL 없이 검색 구현 => 맴버 이름과 주문 상태가 없을 때에도 모든 것을 조회 가능하다. (but 이 방법은 권장하지 않는다.)
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() { // (OrderSimpleApiController의 orderV3를 위해 만들어짐) 패치 조인 이용 => 한방 쿼리로 order를 포함해 member, delivery 까지 조회함
        return em.createQuery("select o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d", Order.class).getResultList();
    }

    public List<OrderSimpleQueryDto> findOrderDtos() { // (OrderSim[leApiController의 orderV4를 위해 만들어짐)
        return em.createQuery("select new jpabook.jpashop.repository.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
                              " join o.member m" +
                              " join o.delivery d", OrderSimpleQueryDto.class).getResultList();
    }

    public List<Order> findAllWithItem() { // OrderApiController의 orderV3을 위해 사용된다. (성능 최적화를 위해 페치 조인 수행 but 데이터가 뻥튀기 된다 => distinct 사용해야함, 컬렉션 페치 조인을 사용할 경우 단점:페이징 불가능(데이터 뻥튀기 때문에, order가 아닌 orderItems 기준으로 페이징한다.) - 또 이경우 메모리에서 소팅을 하기 때문에 데이터가 많으면 out of memory 발생)
        return em.createQuery("select distinct o from Order o" +
                                      " join fetch o.member m" +
                                      " join fetch o.delivery d" +
                                      " join fetch o.orderItems oi" +
                                      " join fetch oi.item i", Order.class).getResultList(); // order가 1개이고 orderItems가 4개인 경우에는 데이터가 4개가 된다.
    }


    public List<Order> findAllWithMemberDelivery2(int offset, int limit) { // (OrderApiController의 orderV3.1를 위해 만들어짐)
        return em.createQuery("select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

//    public List<Order> findAll(OrderSearch orderSearch) { // queryDSL 없이 검색 구현 => JPA criteria 사용 => 이건 유지보수성이 너무 떨어져서 이것도 권장하지 않음
//        ~~~~~~~~~~~
//    }

}
