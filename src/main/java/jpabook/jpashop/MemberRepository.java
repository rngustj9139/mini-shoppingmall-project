package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

//@Repository
//public class MemberRepository {
//
//    @PersistenceContext // 스프링 부트가 자동으로 엔티티 매니저를 주입 시켜준다.
//    private EntityManager em;
//
//    public Long save(Member member) {
//        em.persist(member);
//
//        return member.getId();
//    }
//
//    public Member find(Long id) {
//        return em.find(Member.class, id);
//    }
//
//}
