package com.project.PlanFit.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyPageController {

    @GetMapping("/api/mypage")
    public ResponseEntity<String> getMypage(Authentication authentication) {
        String userId = authentication.getName(); // JwtFilter에서 등록한 userId
        return ResponseEntity.ok("안녕하세요, " + userId + "님! 여기는 마이페이지입니다.");
    }
}
