package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MemberService {

//    @Autowired
//    private MemberRepository memberRepository; // 필드 주입

//    private MemberRepository memberRepository;
//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) { // 세터 주입(필드주입과는 다르게 test를 할때 Mock 같은 것을 주입해줄 수 있다는 장점이 존재한다 but 어플리케이션 로딩시간에 값을 변경시킬 수 있어 일관성이 떨어질 수 있다는 문제점이 존재한다.)
//        this.memberRepository = memberRepository;
//    }

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) { // 생성자 주입, 이부분 작성 안하고 그냥 롬복의 @AllArgsConstructor나 @RequiredArgsConstruct 사용할 수도 있다.
        this.memberRepository = memberRepository;
    }

    // 회원 가입 기능
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);

        return member.getId();
    }

    private void validateDuplicateMember(Member member) { // 이렇게 해도 동시성 문제가 발생할 수 있음
        List<Member> findedMembers = memberRepository.findByName(member.getName());

        if (!findedMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회 기능
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 단일 회원 조회 기능
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    public void update(Long id, String name) { // /api/v2/members/{id}
        Member member = memberRepository.findOne(id); // member는 영속상태이다.
        member.setName(name); // 변경 감지를 이용한 업데이트
    }
}
