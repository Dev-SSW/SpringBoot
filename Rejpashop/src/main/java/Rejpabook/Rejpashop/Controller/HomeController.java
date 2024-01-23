package Rejpabook.Rejpashop.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j //로그 사용
public class HomeController {
    @RequestMapping("/") //요청을 특정 메서드와 매핑하기 위해서 사용, 여기서는 기본 페이지 화면인 "/"을 요청받아 home.html로 매핑
    public String home() {
        log.info("home controller");
        return "home";
    }
}
