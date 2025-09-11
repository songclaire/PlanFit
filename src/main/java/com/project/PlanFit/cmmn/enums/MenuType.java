package com.project.PlanFit.cmmn.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MenuType {

    SCHEDULE,
    LEARNING,
    TRIP,
    RECIPE,
    FOOD,
    MYPAGE;

    @JsonCreator
    public static MenuType from(String value) {
        if (value == null) return null;
        return MenuType.valueOf(value.trim().toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return name();
    }

}
