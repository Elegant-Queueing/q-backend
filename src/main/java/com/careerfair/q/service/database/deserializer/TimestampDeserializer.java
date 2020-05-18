package com.careerfair.q.service.database.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.google.cloud.Timestamp;

import java.io.IOException;

public class TimestampDeserializer extends JsonDeserializer<Timestamp> {
    @Override
    public Timestamp deserialize(JsonParser jsonParser,
                                 DeserializationContext ctxt) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        long seconds = ((IntNode) node.get("seconds")).longValue();
        int nanos = ((IntNode) node.get("nanos")).intValue();
        return Timestamp.ofTimeSecondsAndNanos(seconds, nanos);
    }
}
