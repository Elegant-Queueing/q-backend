package com.careerfair.q.model.redis;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

@Data
public class Student implements Serializable {
    @NonNull private final String id;
    @NonNull private final String name;
}
