package com.careerfair.q.model.db;

import com.careerfair.q.util.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Company {

    @PropertyName("name")
    @JsonProperty("name")
    public String name;

    @PropertyName("roles")
    @JsonProperty("roles")
    public List<Role> roles;

    @PropertyName("employees")
    @JsonProperty("employees")
    public List<String> employees;

    @PropertyName("bio")
    @JsonProperty("bio")
    public String bio;

    @PropertyName("website")
    @JsonProperty("website")
    public String website;
}
