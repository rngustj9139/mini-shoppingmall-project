package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController // @Controller + @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { // @RequestBody => json 데이터를 Member 객체로 매핑해준다. Entity와 api 스펙을 1대1매칭 시키지말고 DTO를 쓰는 것이 좋다. (엔티티의 필드가 바뀌면 api 스펙도 바뀌기 때문에)
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) { // DTO 사용
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() { // 그냥 List 형태로 반환하는 것은 좋지 않다 => 제네릭을 써야한다.
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2() { // List를 그대로 반환하지 않고 제네릭으로 한번 감싼 후 반환
        List<Member> findMembers = memberService.findMembers();

        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName())) // 매핑 수행
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }

    @PutMapping("/api/v2/members/{id}") // 전체 수정
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data // @Getter + @Setter + @ToString + @RequiredArgsConstructor
    static class CreateMemberResponse { // 응답값 (json)

        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }

    }

    @Data
    static class CreateMemberRequest {

        @NotEmpty
        private String name;

    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count; // 데이터의 개수
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

}
