package com.example.collaboration;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class CollabController {

    private final CollabService collabService;

    // 1. 응모 페이지 폼 보여주기
    @GetMapping("/apply")
    public String applyPage(Model model) {
        model.addAttribute("collabEntity", new CollabEntity());
        return "apply";
    }

    // 2. 응모 제출 처리 (유효성 검증 + 중복 방어 로직)
    @PostMapping("/apply")
    public String applyProcess(@Valid @ModelAttribute("collabEntity") CollabEntity collabEntity,
                               BindingResult bindingResult,
                               Model model) {

        // Bean Validation(이름 공백, 전화번호 정규식 등)에 걸린 경우
        if (bindingResult.hasErrors()) {
            return "apply"; // 다시 응모 페이지로 돌아가 에러 출력
        }

        try {
            // 서비스 계층을 통한 등록 및 중복 응모 방어
            collabService.register(collabEntity);
        } catch (IllegalArgumentException e) {
            // 이미 등록된 번호일 경우 서비스에서 던진 예외 메시지를 모델에 담아 전송
            model.addAttribute("errorMessage", e.getMessage());
            return "apply";
        }

        // 성공 시 성공 페이지로 리다이렉트
        return "redirect:/success";
    }

    // 3. 성공 페이지 보여주기
    @GetMapping("/success")
    public String successPage() {
        return "success";
    }

    // 3. 관리자 대시보드 페이지 (응모자 목록 조회)
    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("applicants", collabService.getAllApplicants());
        return "admin";
    }

    // 4. 당첨자 추첨 실행
    @PostMapping("/admin/draw")
    public String drawWinner(Model model) {
        try {
            CollabEntity winner = collabService.drawWinner();
            model.addAttribute("winner", winner); // 당첨자 정보 전달
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        // 목록도 함께 넘겨주어야 화면에 표가 유지됨
        model.addAttribute("applicants", collabService.getAllApplicants());
        return "admin";
    }
}