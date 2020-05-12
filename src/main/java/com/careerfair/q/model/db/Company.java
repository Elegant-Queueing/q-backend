package com.careerfair.q.model.db;

import com.careerfair.q.util.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
public class Company {
    @NonNull private String name;
    @NonNull private List<Role> roles;
    @NonNull private List<String> employees;
    @NonNull private String bio;
    @NonNull private String website;
}
