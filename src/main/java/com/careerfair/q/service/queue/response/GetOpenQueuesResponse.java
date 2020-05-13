package com.careerfair.q.service.queue.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetOpenQueuesResponse {

    @JsonProperty("present")
    private boolean present;

    @JsonProperty("paused")
    private boolean paused;

    @JsonProperty("hasStudentsInWindow")
    private boolean hasStudentsInWindow;

    @JsonProperty("hasStudentsInPhysical")
    private boolean hasStudentsInPhysical;
}
