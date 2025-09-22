package com.project.PlanFit.role.repository;

import com.project.PlanFit.role.entity.UserRole;
import com.project.PlanFit.role.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    // 유저가 가진 권한 이름들 조회 (native: 엔티티 완성 안돼도 안전)
    @Query(value = """
        SELECT r.role_name
          FROM user_role ur
          JOIN role r ON ur.role_id = r.role_id
         WHERE ur.user_id = :userId
           AND (ur.del_yn IS NULL OR ur.del_yn = 'N')
        """, nativeQuery = true)
    List<String> findRoleNamesByUserId(@Param("userId") String userId);

    /**
     * 사용자의 현재 roleId 조회
     */
    @Query("select ur.roleId from UserRole ur where ur.userId = :userId")
    Long findRoleIdByUserId(@Param("userId") String userId);

    /**
     * 사용자의 roleId 업데이트
     */
    // @Modifying할 때는 void나 int/Integer만 반환할 수 있음
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UserRole ur set ur.roleId = :roleId, ur.mdfcnDt = CURRENT_TIMESTAMP where ur.userId = :userId")
    int updateRoleId(@Param("userId") String userId, @Param("roleId") Long roleId);

}
