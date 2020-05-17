package com.careerfair.q.service.database.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.cloud.Timestamp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampDeserializer extends JsonDeserializer<Timestamp> {
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Timestamp deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        //LocalDateTime dt = LocalDateTime.parse(jsonParser.getText(), fmt);
        // System.out.println(dt.toString());
        return Timestamp.ofTimeSecondsAndNanos(12342, 0);
    }
}
