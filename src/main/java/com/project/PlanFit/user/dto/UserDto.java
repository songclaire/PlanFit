package com.project.PlanFit.user.dto;

import com.project.PlanFit.cmmn.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserDto extends BaseDto {
    private String userId;
    private String userName;
    private String password;
    private String email;
    private LocalDate birthDate;
    private String telNo;
    private String roadNmAddr;
    private String roadNmDaddr;
    private LocalDateTime joinDt;
    private LocalDateTime whdwlDt;
    private Long fileId;
    private Boolean isAdmin;

    public static UserDto fromEntity(com.project.PlanFit.user.entity.User u) {
        UserDto d = new UserDto();
        d.setUserId(u.getUserId());
        d.setUserName(u.getUserName());
        d.setEmail(u.getEmail());
        d.setTelNo(u.getTelNo());
        d.setRoadNmAddr(u.getRoadNmAddr());
        d.setRoadNmDaddr(u.getRoadNmDaddr());
        d.setFileId(u.getFileId());
        return d;
    }

}
