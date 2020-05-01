package com.careerfair.q.util.enums;

public enum Role {

    SWE("Software_Engineering"),
    PM("Product_Management"),
    DS("Data_Science");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }
}
