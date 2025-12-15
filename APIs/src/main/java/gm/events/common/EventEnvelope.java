package gm.events.common;

import com.fasterxml.jackson.databind.JsonNode;

public record EventEnvelope(
        String eventType,
        JsonNode payload
) {
}