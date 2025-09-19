package kr.ac.kopo.kyg.projectkyg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class MainController {

    // 서버 메모리에서 과제 저장 (DB 없이)
    private final List<Map<String, Object>> assignments = new ArrayList<>();

    // 초기 더미 데이터 (선택 사항)
    public MainController() {
        Map<String, Object> a1 = new HashMap<>();
        a1.put("subjectName", "딥러닝심화");
        a1.put("studentName", "프로젝트 제출 웹 제작");
        a1.put("description", "테스트 설명 1  테스트 설명 1  테스트 설명 1  테스트 설명 1  테스트 설명 1  테스트 설명 1  ");
        a1.put("submittedAt", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        a1.put("id", 1);

        Map<String, Object> a2 = new HashMap<>();
        a2.put("subjectName", "머신러닝심화");
        a2.put("studentName", "모델활용 자유프로젝트");
        a2.put("description", "테스트 설명 2  테스트 설명 2  테스트 설명 2  테스트 설명 2  테스트 설명 2  테스트 설명 2  ");
        a2.put("submittedAt", LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        a2.put("id", 2);

        assignments.add(a1);
        assignments.add(a2);
    }

    @GetMapping("/main")
    public String main(Model model) {
        model.addAttribute("assignments", assignments);
        return "main";  // main.html 렌더링
    }

    @GetMapping("/submit")
    public String submitForm() {
        return "adp";  // adp.html 렌더링
    }

    @PostMapping("/submit")
    public String submitAssignment(@RequestParam String subjectName,
                                   @RequestParam String studentName,
                                   @RequestParam String description,
                                   @RequestParam String submittedAt) {

        Map<String, Object> newAssignment = new HashMap<>();
        newAssignment.put("id", assignments.size() + 1);
        newAssignment.put("subjectName", subjectName);
        newAssignment.put("studentName", studentName);
        newAssignment.put("description", description);

        // LocalDateTime → String으로 변환하여 Thymeleaf에서 바로 출력 가능
        LocalDateTime dt = LocalDateTime.parse(submittedAt);
        newAssignment.put("submittedAt", dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        assignments.add(newAssignment);

        return "redirect:/main"; // 등록 후 main 페이지로 리다이렉트
    }

    // 과제 상세 페이지 이동
    @GetMapping("/assignment/{id}")
    public String assignmentDetail(@PathVariable int id, Model model) {
        Map<String, Object> selectedAssignment = assignments.stream()
                .filter(a -> a.get("id").equals(id))
                .findFirst()
                .orElse(null);

        if (selectedAssignment == null) {
            return "redirect:/main"; // 없는 과제면 main으로
        }

        model.addAttribute("assignment", selectedAssignment);
        return "sbp";  // sbp.html 렌더링
    }
}
