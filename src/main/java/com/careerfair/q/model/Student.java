package com.careerfair.q.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Student implements Serializable {
    private final int id;
    private final String name;
    // private final List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3));
}
