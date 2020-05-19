package com.careerfair.q.util.enums;

public enum Role {

    SWE("swe"),
    PM("pm"),
    DS("ds");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
