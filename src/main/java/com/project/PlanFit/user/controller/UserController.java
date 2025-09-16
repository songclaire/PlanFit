package com.project.PlanFit.user.controller;

import com.project.PlanFit.user.dto.UserDto;
import com.project.PlanFit.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping(name = "유저 정보 조회", path = "/selectUser")
    public ResponseEntity<UserDto> selectUserInfo(Authentication authentication) {
        String userId = authentication.getName();
        UserDto userInfo = userService.selectUserInfo(userId);
        return ResponseEntity.ok(userInfo);
    }
    
    @PostMapping(name = "유저 정보 수정", path = "/updateUser")
    public ResponseEntity<UserDto> updateUserInfo(
            @AuthenticationPrincipal String authedUserId,
            @RequestBody UserDto dto
    ) throws NotFoundException {

        if (dto.getUserId() != null && !dto.getUserId().equals(authedUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId mismatch");
        }

        UserDto updated = userService.updateUserInfo(authedUserId, dto);
        return ResponseEntity.ok(updated);
    }

}
