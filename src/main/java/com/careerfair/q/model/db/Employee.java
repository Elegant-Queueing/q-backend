package com.careerfair.q.model.db;

import com.careerfair.q.util.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
public class Employee {

    @NonNull private String employee_id;
    @NonNull private Role role;
    @NonNull private String name;
    @NonNull private String company_id;
    @NonNull private String bio;
    @NonNull private String email;
    @NonNull private List<String> students;
}
