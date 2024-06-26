package study.ORM.Controller.Api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import study.ORM.Entity.Member;
import study.ORM.Service.MemberService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    /** 회원 등록 */
    @PostMapping("api/members")
    public CreateMemberResponse SaveMember(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.username); //request Dto를 통해 받아온 이름으로 설정
        Long id = memberService.join(member); //회원가입
        return new CreateMemberResponse(id);
    }
    //DTO
    @Data
    static class CreateMemberRequest {
        @NotEmpty //이름이 비어있으면 안되도록 설정
        private String username;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;
        private CreateMemberResponse(Long id) { this.id = id;}
    }

    /** 회원 수정 */
    @PutMapping("api/members/{id}")
    public UpdateMemberResponse UpdateMember(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getUsername());  //Request DTO로 받아온 이름으로 수정
        Member findMember = memberService.findOne(id);    //커맨드와 쿼리 분리
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }
    //DTO
    @Data
    static class UpdateMemberRequest {
        private String username;
    }

    @Data @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    /** 회원 조회 */
    @GetMapping("api/members")
    public FindMemberResponse FindMember() {
        List<Member> findmembers = memberService.findMembers();
        List<FindMemberRequest> collect = findmembers.stream()
                .map(m -> new FindMemberRequest(m.getName()))
                .collect(Collectors.toList());
        return new FindMemberResponse(collect);
    }
    // DTO
    @Data @AllArgsConstructor
    static class FindMemberRequest {
        private String username;
    }

    @Data @AllArgsConstructor
    static class FindMemberResponse<T> { //감싸서 보내기 때문에 유연성이 생긴다
        private T data;
    }
}
