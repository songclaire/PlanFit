package com.project.PlanFit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TokenResponseDto {
    private String token;
    private String userName;
    private List<String> roles;
}
