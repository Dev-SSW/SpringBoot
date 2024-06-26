package jpabook.jpashop.Api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.Domain.Member;
import jpabook.jpashop.Service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController //@Controller + @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;
    //API, DTO 적용

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    // **회원 등록 API** //
    //@RequestBody를 통해 메소드 파라미터를 Binding한다 (제이슨 데이터를 멤버로 바꿔준다) //값 넣기 post
    @PostMapping("api/v1/members")
    //@Valid는 @RequestBody Annotation으로 Mapping되는 Java 객체의 유효성 검증을 수행하는 Annotation이다
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    } //Member 클래스의 name -> username으로 바뀌면 스펙 자체가 바뀌어 버리는 문제가 있다
      // (Member 클래스에서 직접 받아오기 때문에 꼭 입력해야하는 이름이나 주소 등을 설정할 때 @NotEmpty설정을 엔티티 단에서 해야하는 문제도 있다)
      // -> DTO를 통해 별도로 받는 것이 좋다

    @PostMapping("api/v2/members") //더 안전한 방법
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.name); //받아온 name 값을 만들어진 새로 만든 member 객체의 이름으로 설정
        //만약 Member 클래스의 name이 username으로 바뀌었다면 오류가 나기에 member.setusername으로 바꾸기만 하면 된다. (오류 방지 가능)
        Long id = memberService.join(member); //join으로 회원가입시킴
        return new CreateMemberResponse(id); //id를 리턴
    }

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    // DTO
    @Data
    // DTO (Member에서 어떤 변수들만 받아오는지 알 수 없고, 다른 변수들도 service나 repository 등 어떤 곳에서 값이 채워져 오는지
    // 알 수 없기 때문에 DTO를 통해 어떤 변수만을 받아오는지 알 수 있고, NotEmpty를 통해 어떤 것에서만 비어있으면 안되는지를 결정할 수 있다.
    // API 스펙에서 모든 것을 알 수 있기에 큰 장점, 엔티티(domain)를 외부에 노출하지 않는다
    static class CreateMemberRequest { //name 값만 받아옴
        @NotEmpty //값이 없으면 send 할 수 없도록 함
        private String name;

    }

    @Data //@getter + @setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
    static class CreateMemberResponse {
        private Long id;
        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    // **회원 수정 API** //
    // 수정 PutMapping
    @PutMapping("api/v2/members/{id}")
    public UpdateMemberResponse UpdateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());  //id와 request에서 받아온 name으로 이름을 수정
        Member findMember = memberService.findOne(id); //커맨드랑 쿼리를 분리하는 방법
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    // DTO

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    // **회원 조회 API** //

    @GetMapping("api/v1/members") //jsonignore로 요청하지 않은 제이슨이 보내지지 않도록 막을 순 있지만, 엔티티에 화면을 위한 로직이 추가되야하고,
    public List<Member> memberV1() {//추가되지 않는다면 엔티티의 모든 값들이 노출될 수 있기에 좋지 않은 방법이다
        return memberService.findMembers();
    }

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

    @GetMapping("api/v2/members") //@jsonignore이 없어도 노출하고 싶은 것만 노출할 수 있다
    public Result memberV2() { //더 안전한 방법
        List<Member> findmembers = memberService.findMembers();
        List<MemberDto> collect = findmembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect);
        //Member를 MemberDto로 바꾸어서 반환 (이름만 받아왔음)
        //return new Result(collect.size(), collect) count 값도 같이 보낼 수 있는 유연성이 생긴다
    }

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    // DTO

    @Data @AllArgsConstructor
    static class Result<T> { //한번 감싸서 보내기 때문에 유연성이 생긴다
        private T data;
    }

    @Data @AllArgsConstructor
    static class MemberDto { //받아올 것들만 저장해서 받아온다
        private String name;
    }
}
