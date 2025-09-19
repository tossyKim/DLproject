package kr.ac.kopo.kyg.projectkyg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class MainController {

    // 서버 메모리에서 팀 저장 (DB 없이)
    private final List<Map<String, Object>> teams = new ArrayList<>();

    // 현재 사용자 참가 팀 목록 (임시, 로그인 구현 전용)
    private final Map<String, List<Map<String, Object>>> userTeams = new HashMap<>();

    // 서버 메모리에서 과제 저장 (팀별)
    private final Map<Integer, List<Map<String, Object>>> teamAssignments = new HashMap<>();

    @GetMapping("/main")
    public String main(Model model) {
        String currentUser = "kim"; // 임시 사용자
        List<Map<String, Object>> joinedTeams = userTeams.getOrDefault(currentUser, new ArrayList<>());
        model.addAttribute("teams", joinedTeams);
        return "main";  // main.html 렌더링
    }

    // 팀 만들기 페이지
    @GetMapping("/teams/create")
    public String createTeamForm() {
        return "createTeam";  // createTeam.html 렌더링
    }

    // 팀 만들기 처리
    @PostMapping("/teams/create")
    public String createTeam(@RequestParam String teamName) {
        Map<String, Object> newTeam = new HashMap<>();
        newTeam.put("id", teams.size() + 1);
        newTeam.put("name", teamName);
        teams.add(newTeam);

        // 만든 팀을 현재 사용자에게 자동 참가
        String currentUser = "kim";
        userTeams.computeIfAbsent(currentUser, k -> new ArrayList<>()).add(newTeam);

        // 더미 과제 데이터 생성
        List<Map<String, Object>> assignments = new ArrayList<>();
        Map<String, Object> a1 = new HashMap<>();
        a1.put("id", 1);
        a1.put("name", "첫 번째 과제");
        a1.put("description", "팀 과제 예시 1");
        assignments.add(a1);

        Map<String, Object> a2 = new HashMap<>();
        a2.put("id", 2);
        a2.put("name", "두 번째 과제");
        a2.put("description", "팀 과제 예시 2");
        assignments.add(a2);

        teamAssignments.put((Integer) newTeam.get("id"), assignments);

        return "redirect:/main";
    }

    // 팀 참가 페이지
    @GetMapping("/teams/join")
    public String joinTeamForm(Model model) {
        model.addAttribute("teams", teams); // 전체 팀 목록 보여주기
        return "joinTeam";  // joinTeam.html 렌더링
    }

    // 팀 참가 처리
    @PostMapping("/teams/join")
    public String joinTeam(@RequestParam int teamId) {
        String currentUser = "kim";
        Map<String, Object> selectedTeam = teams.stream()
                .filter(t -> t.get("id").equals(teamId))
                .findFirst()
                .orElse(null);

        if (selectedTeam != null) {
            userTeams.computeIfAbsent(currentUser, k -> new ArrayList<>()).add(selectedTeam);
        }

        return "redirect:/main";
    }

    // 팀 과제 목록 페이지 또는 프로젝트 추가 페이지 분기
    @GetMapping("/projects/{teamId}")
    public String projectList(@PathVariable String teamId, Model model) {
        // teamId가 "add"인 경우 프로젝트 추가 페이지로 이동
        if ("add".equalsIgnoreCase(teamId)) {
            return "addproject"; // addproject.html 렌더링
        }

        // 숫자인 경우만 파싱
        int id;
        try {
            id = Integer.parseInt(teamId);
        } catch (NumberFormatException e) {
            return "redirect:/main"; // 잘못된 값이면 메인으로
        }

        Map<String, Object> team = teams.stream()
                .filter(t -> t.get("id").equals(id))
                .findFirst()
                .orElse(null);

        if (team == null) {
            return "redirect:/main"; // 없는 팀이면 메인으로
        }

        List<Map<String, Object>> assignments = teamAssignments.getOrDefault(id, new ArrayList<>());

        model.addAttribute("team", team);
        model.addAttribute("assignments", assignments);

        return "projects"; // projects.html 렌더링
    }
}
