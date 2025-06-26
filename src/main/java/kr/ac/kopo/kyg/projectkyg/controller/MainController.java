package kr.ac.kopo.kyg.projectkyg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {  // 클래스명 첫글자 대문자

    @GetMapping("/main")
    public String requestAddBookForm(Model model) {
        return "main";  // 뷰 이름 main.html 반환
    }
}
