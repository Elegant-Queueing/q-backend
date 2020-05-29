package com.careerfair.q.model.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Fair {

    @PropertyName("fair_id")
    @JsonProperty("fair_id")
    public String fairId;

    @PropertyName("name")
    @JsonProperty("name")
    public String name;

    @PropertyName("university_id")
    @JsonProperty("university_id")
    public String universityId;

    @PropertyName("desc")
    @JsonProperty("description")
    public String description;

    @PropertyName("companies")
    @JsonProperty("companies")
    public List<String> companies;

    @PropertyName("start_time")
    @JsonProperty("start_time")
    public Timestamp startTime;

    @PropertyName("end_time")
    @JsonProperty("end_time")
    public Timestamp endTime;
}
