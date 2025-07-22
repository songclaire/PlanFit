package com.project.PlanFit.user.controller;

import com.project.PlanFit.cmmn.jwt.JwtProvider;
import com.project.PlanFit.user.dto.LoginRequestDto;
import com.project.PlanFit.user.dto.TokenResponseDto;
import com.project.PlanFit.user.dto.UserDto;
import com.project.PlanFit.user.entity.User;
import com.project.PlanFit.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    public final UserService userService;
    public final JwtProvider jwtProvider;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto dto) {
        return userService.signup(dto);
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto request) {
        // 1. 사용자 검증
        User user = userService.validateUser(request.getUserId(), request.getPassword());

        // 2. JwtProvider에서 생성한 토큰 발급
        String token = jwtProvider.createToken(user.getUserId());

        // 클라이언트에게 토큰 응답 보내기
        return ResponseEntity.ok(new TokenResponseDto(token, user.getUserName()));
    }

}
