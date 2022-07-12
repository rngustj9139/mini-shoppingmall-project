package jpabook.jpashop.repository.query;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository { // OrderApiController의 orderV4를 위해 사용

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        return em.createQuery("select new jpabook.jpashop.repository.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
                               " join o.member m" +
                               " join o.delivery d", OrderQueryDto.class).getResultList();
    }

}
