package study.Spring_Login.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import study.Spring_Login.DTO.JoinRequest;
import study.Spring_Login.DTO.LoginRequest;
import study.Spring_Login.Domain.Member;
import study.Spring_Login.Domain.MemberRole;
import study.Spring_Login.Service.MemberService;

@Controller @RequiredArgsConstructor
public class SessionLoginController {
    private final MemberService memberService;
    @GetMapping(value = {"session-login"})
    public String home(Model model, @SessionAttribute(name = "memberId", required = false) Long memberId) {
        // 화면에 출력하기 위해 model에 속성 추가
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션로그인");
        Member loginMember = memberService.getLoginMemberById(memberId);
        // 로그인 되어있다면 model에 이름 속성 추가
        if (loginMember != null) {
            model.addAttribute("name", loginMember.getName());
        }
        return "home";
    }
    @GetMapping("session-login/join")
    public String joinPage(Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션로그인");
        // 회원가입을 위해서 model 통해서 joinRequest 전달
        model.addAttribute("joinRequest", new JoinRequest());
        return "join";
    }
    @PostMapping("session-login/join")
    public String join(@Valid @ModelAttribute JoinRequest joinRequest, BindingResult bindingResult, Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션로그인");
        // ID 중복 여부 확인
        if (memberService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
            bindingResult.addError(new FieldError("joinRequest", "loginId", "ID가 존재합니다."));
        }
        // 비밀번호 = 비밀번호 체크 여부 확인
        if (!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "비밀번호가 일치하지 않습니다."));
        }
        // 에러가 존재할 시 다시 join.html로 전송
        if (bindingResult.hasErrors()) {
            return "join";
        }
        // 에러가 존재하지 않을 시 joinRequest 통해서 회원가입 완료
        memberService.join(joinRequest);
        // 회원가입 시 홈 화면으로 이동
        return "redirect:/session-login";
    }
    @GetMapping("session-login/login")
    public String loginPage(Model model) {

        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션로그인");

        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("session-login/login")
    public String login(@ModelAttribute LoginRequest loginRequest, BindingResult bindingResult, HttpServletRequest request, Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션로그인");
        Member member = memberService.login(loginRequest);
        // ID나 비밀번호가 틀린 경우 global error return
        if (member == null) {
            bindingResult.reject("loginFail", "로그인 아이디 또는 비밀번호가 틀렸습니다.");
        }
        if (bindingResult.hasErrors()) {
            return "login";
        }
        // ===== 로그인 성공 => 세션 생성 및 속성 설정 =====
        // 기존의 세션을 무효화
        request.getSession().invalidate();
        // 세션 생성 => request에 연관된 세션이 없을 시 새로운 세션 생성 후 반환
        HttpSession session = request.getSession(true);
        // 세션에 {"memberId", memberId} 속성 추가
        session.setAttribute("memberId", member.getId());
        // 세션의 유효기간을 1시간으로 설정
        session.setMaxInactiveInterval(60 * 60);
        // =========================================
        return "redirect:/session-login";
    }
    @GetMapping("session-login/logout")
    public String logout(HttpServletRequest request, Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션로그인");
        // request와 연관된 세션 불러옴 (없으면 null 반환)
        HttpSession session = request.getSession(false);
        // 세션이 존재 ( : 로그인 되어있다는 뜻)
        if (session != null) {
            // 로그인 된 세션 무효화
            session.invalidate();
        }
        return "redirect:/session-login";
    }
    @GetMapping("session-login/info")
    public String memberInfo(@SessionAttribute(name = "memberId", required = false) Long memberId, Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션로그인");
        Member loginMember = memberService.getLoginMemberById(memberId);
        if (loginMember == null) {
            return "redirect:/session-login/login";
        }
        model.addAttribute("member", loginMember);
        return "info";
    }
    @GetMapping("session-login/admin")
    public String adminPage(@SessionAttribute(name = "memberId", required = false) Long memberId, Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");
        Member loginMember = memberService.getLoginMemberById(memberId);
        if(loginMember == null) {
            return "redirect:/login";
        }
        if(!loginMember.getRole().equals(MemberRole.ADMIN)) {
            return "redirect:/session-login";
        }
        return "admin";
    }
}
