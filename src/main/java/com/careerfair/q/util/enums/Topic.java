package com.careerfair.q.util.enums;

public enum Topic {

    /**
     * The topic to subscribe to get updates on when a queue has opened or closed
     */
    QUEUE("queues"),

    /**
     * The topic to subscribe to get updates for updates related to SWE role
     */
    SWE("swe"),

    /**
     * The topic to subscribe to get updates for updates related to PM role
     */
    PM("pm"),

    /**
     * The topic to subscribe to get updates for updates related to DS role
     */
    DS("ds");

    private final String topic;

    Topic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
