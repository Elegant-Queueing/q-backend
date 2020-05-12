package com.careerfair.q.model.db;

import com.google.cloud.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
public class Fair {

    @NonNull private String fairId;
    @NonNull private List<String> companies;
    @NonNull private String desc;
    @NonNull private Timestamp end_time;
    @NonNull private String name;
    @NonNull private Timestamp start_time;
    @NonNull private String university_id;
}
