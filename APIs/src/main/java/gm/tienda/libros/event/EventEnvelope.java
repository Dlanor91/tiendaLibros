package gm.tienda.libros.event;

import com.fasterxml.jackson.databind.JsonNode;
import gm.tienda.libros.event.enums.EventType;

public record EventEnvelope(
        EventType eventType,
        JsonNode payload
) {
}