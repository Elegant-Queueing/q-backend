package com.careerfair.q.model.db;

import com.careerfair.q.util.enums.Role;
import com.google.cloud.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
public class Student {

    @NonNull private String first_name;
    @NonNull private String last_name;
    @NonNull private String university_id;
    @NonNull private String major;
    @NonNull private Role role;
    @NonNull private String bio;
    @NonNull private String email;
    @NonNull private double gpa;
    @NonNull private Timestamp grad_date;
    @NonNull private boolean international;
    @NonNull private List<String> employees;
}
