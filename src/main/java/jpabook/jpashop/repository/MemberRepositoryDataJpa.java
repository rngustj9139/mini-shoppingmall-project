package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepositoryDataJpa extends JpaRepository<Member, Long> { // 구현체는 Spring Data Jpa가 알아서 만들어 주입해준다. (findAll 등등 다 이미 구현되어 있다.) (optional로 반환하기 때문에 서비스계층에서 .get()을 써야한다.)

    List<Member> findByName(String name); // 구현 안해도 된다. Spring Data Jpa가 알아서 구현해준다.

}
