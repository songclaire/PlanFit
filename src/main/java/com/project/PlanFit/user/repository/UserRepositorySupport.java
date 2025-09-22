package com.project.PlanFit.user.repository;

import com.project.PlanFit.user.dto.UserDto;

import java.util.List;

public interface UserRepositorySupport {

    List<UserDto> selectUserList(UserDto dto);

}
