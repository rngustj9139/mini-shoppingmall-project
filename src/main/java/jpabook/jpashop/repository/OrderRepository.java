package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
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

    public List<Order> findAll(OrderSearch orderSearch) { // queryDSL 없이 검색 구현 => but 멤버 이름과 주문 상태가 없을 때에는 모든 것을 조회해야함 => 동적 쿼리가 필요함
        return em.createQuery("select o from Order o join o.member m" + // jpql에서의 조인 방법임
                " where o.status = :status" +
                " and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000) // 페이징 할때 가져울 개수 (최대 1000건)
                .getResultList();
    }

}
