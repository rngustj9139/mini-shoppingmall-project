package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) // 스프링을 써서 테스트 할거라고 선언
@SpringBootTest // 스프링 부트를 써서 통합 테스트를 수행
public class MemberRepositoryTest {

    @Autowired // 의존관계 자동 주입
    MemberRepository memberRepository;

    @Test
    @Transactional // jpa는 transaction이라는 단위 속에서 실행되서 이 어노테이션을 붙여야함 하지만 이 어노테이션이 test에 있으면 db에 저장을 해도 롤백 시켜버린다. 롤백하기 싫으면 @Rollback(false) 쓰면된다.
    @Rollback(false)
    public void testMember() throws Exception {
        Member member = new Member();
        member.setUsername("memberA");
        Long savedId = memberRepository.save(member);

        Member findedMember = memberRepository.find(savedId);

        Assertions.assertThat(findedMember).isEqualTo(member);
    }

}