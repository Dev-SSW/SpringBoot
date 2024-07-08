package study.Spring_Login.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import study.Spring_Login.DTO.JoinRequest;
import study.Spring_Login.DTO.LoginRequest;
import study.Spring_Login.Domain.Member;
import study.Spring_Login.Domain.MemberRole;
import study.Spring_Login.Service.MemberService;

@Controller @RequiredArgsConstructor
public class CookieLoginController {
    private final MemberService memberService;
    @GetMapping(value = {"cookie-login"})
    public String home(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {
        // 화면에 출력하기 위해 model에 속성 추가
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");
        Member loginMember = memberService.getLoginMemberById(memberId);
        // 로그인 되어있다면 model에 이름 속성 추가
        if (loginMember != null) {
            model.addAttribute("name", loginMember.getName());
        }
        return "home";
    }

    @GetMapping("cookie-login/join")
    public String joinPage(Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");
        // 회원가입을 위해서 model 통해서 joinRequest 전달
        model.addAttribute("joinRequest", new JoinRequest());
        return "join";
    }

    @PostMapping("cookie-login/join")
    public String join(@Valid @ModelAttribute JoinRequest joinRequest, BindingResult bindingResult, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");
        // ID 중복 여부 확인
        if(memberService.checkLoginIdDuplicate(joinRequest.getLoginId())){
            bindingResult.addError(new FieldError(
                    "joinRequest",
                    "loginId",
                    "ID가 존재합니다."));
        }
        // 비밀번호 = 비밀번호 체크 여부 확인
        if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())){
            bindingResult.addError(new FieldError(
                    "joinRequest",
                    "passwordCheck",
                    "비밀번호가 일치하지 않습니다"));
        }
        // 에러가 존재할 시 다시 join.html로 전송
        if (bindingResult.hasErrors()) {
            return "join";
        }
        // 에러가 존재하지 않을 시 joinRequest 통해서 회원가입 완료
        memberService.join(joinRequest);
        // 회원가입 시 홈화면으로 이동
        return "redirect:/cookie-login";
    }
    @GetMapping("cookie-login/login")
    public String loginPage(Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("cookie-login/login")
    public String login(@ModelAttribute LoginRequest loginRequest, BindingResult bindingResult, HttpServletResponse response, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");
        Member member = memberService.login(loginRequest);
        // ID나 비밀번호가 틀린 경우 global error return
        if(member == null) {
            bindingResult.reject("loginFail", "로그인 아이디 또는 비밀번호가 틀렸습니다.");
        }
        if(bindingResult.hasErrors()) {
            return "login";
        }
        // 로그인 성공 => 쿠키 생성
        Cookie cookie = new Cookie("memberId", String.valueOf(member.getId()));
        cookie.setMaxAge(60 * 60);  // 쿠키 유효 시간 : 1시간
        response.addCookie(cookie);
        return "redirect:/cookie-login";
    }

    @GetMapping("cookie-login/logout")
    public String logout(HttpServletResponse response, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");
        // 동일한 이름의 새 쿠키 생성 => 로그아웃
        Cookie cookie = new Cookie("memberId", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/cookie-login";
    }

    @GetMapping("cookie-login/info")
    public String info(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");
        Member loginMember = memberService.getLoginMemberById(memberId);
        if(loginMember == null) {
            return "redirect:/login";
        }
        model.addAttribute("member", loginMember);
        return "info";
    }

    @GetMapping("cookie-login/admin")
    public String adminPage(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");
        Member loginMember = memberService.getLoginMemberById(memberId);
        if(loginMember == null) {
            return "redirect:/login";
        }
        if(!loginMember.getRole().equals(MemberRole.ADMIN)) {
            return "redirect:/cookie-login";
        }
        return "admin";
    }
}
