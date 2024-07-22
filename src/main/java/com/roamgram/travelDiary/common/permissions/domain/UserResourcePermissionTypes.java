package com.roamgram.travelDiary.common.permissions.domain;

import lombok.Getter;

@Getter
public enum UserResourcePermissionTypes {
    VIEW(1),
    CLONE(2),
    PARTICIPANT(2),
    EDITOR(3),
    OWNER(4);

    private final int level;

    UserResourcePermissionTypes(int level) {
        this.level = level;
    }

    public boolean hasHigherOrEqualPermission(UserResourcePermissionTypes other) {
        return this.level >= other.level;
    }

    public static UserResourcePermissionTypes fromLevel(int level) {
        for (UserResourcePermissionTypes type : values()) {
            if (type.level == level) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid permission level: " + level);
    }
}

