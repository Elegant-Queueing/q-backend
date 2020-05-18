package com.careerfair.q.util.enums;

public enum Role {

    SWE("swe", Topic.SWE),
    PM("pm", Topic.PM),
    DS("ds", Topic.DS);

    private final String role;
    private final Topic topic;

    Role(String role, Topic topic) {
        this.role = role;
        this.topic = topic;
    }

    public String getRole() {
        return this.role;
    }

    public Topic getTopic() {
        return topic;
    }
}
