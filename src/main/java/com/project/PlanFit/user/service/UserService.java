package com.project.PlanFit.user.service;

import com.project.PlanFit.user.dto.UserDto;
import com.project.PlanFit.user.entity.User;
import com.project.PlanFit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    public final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 토큰
    public User validateUser(String userId, String password) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디입니다."));

        System.out.println("✅ 사용자 찾음: " + user.getUserId());
        System.out.println("✅ 저장된 비번: " + user.getPassword());
        System.out.println("✅ 입력한 비번: " + password);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }


    public ResponseEntity<?> signup(UserDto dto) {

        // 1. UserDto → User로 변환
        User user = User.builder()
                .userId(dto.getUserId())
                .userName(dto.getUserName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .birthDate(dto.getBirthDate())
                .telNo(dto.getTelNo())
                .roadNmAddr(dto.getRoadNmAddr())
                .roadNmDaddr(dto.getRoadNmDaddr())
                .joinDt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "userName", user.getUserName()
        ));
    }
}
