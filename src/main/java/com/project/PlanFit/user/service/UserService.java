package com.project.PlanFit.user.service;

import com.project.PlanFit.file.repository.FileAtmtRepository;
import com.project.PlanFit.role.entity.UserRole;
import com.project.PlanFit.role.repository.UserRoleRepository;
import com.project.PlanFit.user.dto.UserDto;
import com.project.PlanFit.user.entity.User;
import com.project.PlanFit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    public final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileAtmtRepository fileAtmtRepository;
    private final UserRoleRepository userRoleRepository;

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

    /**
     * 회원가입
     */
    @Transactional
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

        // 2. USER_ROLE 저장
        userRoleRepository.save(
                UserRole.builder()
                        .userId(user.getUserId())
                        .roleId(2)
                        .delYn("N")
                        .regId(user.getUserId())
                        .mdfrId(user.getUserId())
                        .regDt(LocalDateTime.now())
                        .mdfcnDt(LocalDateTime.now())
                        .build()
        );

        return ResponseEntity.ok(Map.of(
                "userName", user.getUserName()
        ));
    }

    /**
     * 유저 정보 호출
     */
    public UserDto selectUserInfo(String userId) {
        User user = userRepository.findById(userId)   // PK가 userId일 경우
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setTelNo(user.getTelNo());
        dto.setRoadNmAddr(user.getRoadNmAddr());
        dto.setRoadNmDaddr(user.getRoadNmDaddr());
        dto.setFileId(user.getFileId());

        return dto;
    }

    /**
     * 유저 정보 수정
     */
    @Transactional
    public UserDto updateUserInfo(String targetUsreId, UserDto dto) throws NotFoundException {
        User userInfo = userRepository.findById(dto.getUserId())
                                .orElseThrow(() -> new NotFoundException("user"));
        // 기존에 첨부파일이 있을 경우
        Long oldFileId = userInfo.getFileId();
        Long newFileId = dto.getFileId();

        // 필드 업데이트
        userInfo.patchProfile(
                dto.getUserName(),
                dto.getEmail(),
                dto.getTelNo(),
                dto.getRoadNmAddr(),
                dto.getRoadNmDaddr(),
                newFileId
        );

        // 파일 업데이트
        if (!Objects.equals(oldFileId, newFileId)) {
            if (newFileId != null) {
                fileAtmtRepository.updateTmprStrgYn(newFileId);
            }
        }
        return UserDto.fromEntity(userInfo);

    }

    /**
     * 유저 목록 조회
     */
    @Transactional
    public List<UserDto> selectUserList(UserDto dto) {
        return userRepository.selectUserList(dto);
    }

    /**
     * 유저 권한 변경
     */
    @Transactional
    public void updateAdminRole(UserDto dto) {
        Long curRoleId = userRoleRepository.findRoleIdByUserId(dto.getUserId());
        Long next = (curRoleId != null && curRoleId == 1L) ? 2L : 1L;
        userRoleRepository.updateRoleId(dto.getUserId(), next);
    }
}
