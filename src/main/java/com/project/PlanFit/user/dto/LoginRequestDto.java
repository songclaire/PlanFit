// 로그인할 때 사용자가 입력한 ID/PW를 받기 위한 DTO
package com.project.PlanFit.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class LoginRequestDto {
    private String userId;
    private String password;
}
