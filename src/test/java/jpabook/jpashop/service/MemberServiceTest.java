package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest // 스프링부트와 통합해서 테스트 수행
@Transactional // 이게 존재해야 롤백이 된다.
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    // @Rollback(false)
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("Kim");

        // when
        Long savedId = memberService.join(member);

        // then
        Assertions.assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("Kim1");

        Member member2 = new Member();
        member2.setName("Kim1");

        // when
        memberService.join(member1);
        try { // try, catch문 대신 @Test(expected = IllegalStateException.class) 이용하면 된다.
            memberService.join(member2);
        } catch (IllegalStateException e) {
            return; // 예외가 발생하고 정상적으로 리턴
        }

        // then
        Assertions.fail("예외가 발생해야 한다.");
    }

}