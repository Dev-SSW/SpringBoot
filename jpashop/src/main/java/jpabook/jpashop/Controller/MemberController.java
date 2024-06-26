package jpabook.jpashop.Controller;

import jakarta.validation.Valid;
import jpabook.jpashop.Service.MemberService;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }
    @PostMapping("/members/new") //Valid을 통해 MemberForm에 있는 NotEmpty와 같은 어노테이션이 적용되도록 만들어줌
    public String create(@Valid MemberForm form, BindingResult result) {
        if(result.hasErrors()) { //@NotEmpty 어노테이션이 작동되게 된다.
            return "members/createMemberForm";
        }
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);
        memberService.join(member);
        return "redirect:/";
    }
    @GetMapping("/members")
    public String list(Model model) {
        model.addAttribute("members" , memberService.findMembers());
        return "members/memberList";
    }
}
