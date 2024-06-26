package study.ORM.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import study.ORM.Controller.Form.MemberForm;
import study.ORM.Entity.Address;
import study.ORM.Entity.Member;
import study.ORM.Service.MemberService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    // mebers/new 주소 요청을 받으면 createMemberForm.html으로 이동 ->
    // PostMapping을 통해 회원 정보를 저장 후 다시 "/" 페이지로 돌려보냄
    // members 주소 요청을 받으면 memberList.html로 이동 -> 목록 확인

    //**회원등록**//
    @GetMapping(value = "/members/new") //주로 조회할 때 사용, 서버의 리소스를 조회할 때 사용합니다.
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping(value = "/members/new") //주로 노출하면 안되는 데이터를 저장할 때 사용, 서버에 리소스를 등록(저장)할 때 사용합니다.
    public String create(@Valid MemberForm form, BindingResult result) { //valid는 request body를 검증, BindingResult은 검증 오류 저장 객체
        if (result.hasErrors()) { return "members/createMemberForm"; }
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);
        memberService.join(member);
        return "redirect:/";
    }

    //**회원 목록 조회**//
    @GetMapping(value = "/members") //조회한 상품을 뷰에 전달하기 위해 스프링 MVC가 제공하는 모델 객체에 저장, 실행할 뷰 이름을 반환
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
