package com.careerfair.q.model.redis;

import lombok.Data;

import java.io.Serializable;

@Data
public class Student implements Serializable {
    private final String id;
    private final String name;
}
