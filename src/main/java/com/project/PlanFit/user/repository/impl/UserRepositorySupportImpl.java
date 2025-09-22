package com.project.PlanFit.user.repository.impl;

import com.project.PlanFit.role.entity.QRole;
import com.project.PlanFit.role.entity.QUserRole;
import com.project.PlanFit.user.dto.UserDto;
import com.project.PlanFit.user.entity.QUser;
import com.project.PlanFit.user.repository.UserRepositorySupport;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositorySupportImpl implements UserRepositorySupport {

    // 스프링이 JPA의 핵심 객체(EntityManager)를 넣어줌
    @PersistenceContext
    private EntityManager em;

    private JPAQueryFactory qf() {
        return new JPAQueryFactory(em);
    }

    @Override
    public List<UserDto> selectUserList(UserDto dto) {
        QUser qUser = QUser.user;
        QUserRole qUr = QUserRole.userRole;
        QRole qRole = QRole.role;

        BooleanBuilder where = new BooleanBuilder();

        if (dto != null) {
            if (dto.getUserId() != null && !dto.getUserId().isBlank()) {
                where.and(qUser.userId.containsIgnoreCase(dto.getUserId().trim()));
            }
            if (dto.getUserName() != null && !dto.getUserName().isBlank()) {
                where.and(qUser.userName.containsIgnoreCase(dto.getUserName().trim()));
            }
            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                where.and(qUser.email.containsIgnoreCase(dto.getEmail().trim()));
            }
            if (dto.getTelNo() != null && !dto.getTelNo().isBlank()) {
                where.and(qUser.telNo.containsIgnoreCase(dto.getTelNo().trim()));
            }
        }

        return qf()
                .select(Projections.fields(UserDto.class,
                        qUser.userId,
                        qUser.userName,
                        qUser.password,
                        qUser.email,
                        qUser.birthDate,
                        qUser.telNo,
                        qUser.roadNmAddr,
                        qUser.roadNmDaddr,
                        qUser.joinDt,
                        qUser.whdwlDt,
                        qUser.fileId,
                        JPAExpressions.selectOne()
                                .from(qUr)
                                .join(qRole).on(qRole.roleId.eq(qUr.roleId))
                                .where(
                                    qUr.userId.eq(qUser.userId),
                                    qUr.delYn.isNull().or(qUr.delYn.eq("N")),
                                    qRole.roleName.equalsIgnoreCase("ADMIN")
                                ).exists()
                                .as("isAdmin")
                ))
                .from(qUser)
                .where(where)
                .orderBy(qUser.joinDt.desc(), qUser.userId.asc())
                .fetch();
    }

}
