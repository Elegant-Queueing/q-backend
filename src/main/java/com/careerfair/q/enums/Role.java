package com.careerfair.q.enums;

public enum Role {

    SWE("Software Engineering"),
    PM("Product Management"),
    DS("Data Science");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }
}
