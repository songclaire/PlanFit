package com.project.PlanFit.role.entity;

import lombok.*;
import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class UserRoleId implements Serializable {
    private String  userId;   // user.user_id
    private Integer roleId;   // role.role_id
}
