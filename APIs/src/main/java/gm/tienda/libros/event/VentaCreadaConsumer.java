package gm.tienda.libros.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import gm.tienda.libros.event.enums.EventType;
import gm.tienda.libros.service.imp.LibroService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VentaCreadaConsumer {

    private final LibroService libroService;
    private final KafkaTemplate<String, EventEnvelope> kafkaTemplate;//lo inicio porq envio
    private final ObjectMapper objectMapper;

    public VentaCreadaConsumer(LibroService libroService, KafkaTemplate<String, EventEnvelope> kafkaTemplate,
                               ObjectMapper objectMapper) {
        this.libroService = libroService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "ventas.eventos")
    public void consumirEvento(ConsumerRecord<String, String> record) {

        Integer ventaId = null;

        try {
            // JSON crudo
            EventEnvelope event = objectMapper.readValue(record.value(), EventEnvelope.class);

            if (event.eventType().equals(EventType.VENTA_CREADA)) {
                // Payload → VentaCreadaEvent
                VentaRecibidaEvent venta = objectMapper.convertValue(event.payload(), VentaRecibidaEvent.class);

                ventaId = venta.idVenta();

                // Lógica de negocio
                libroService.rebajarStock(venta.isbnLibro(), venta.cantidad());

                //Respuesta OK
                EventEnvelope respuesta = new EventEnvelope(
                        EventType.STOCK_COMPLETADO,
                        objectMapper.valueToTree(
                                new StockRespuestaEvent(
                                        ventaId,
                                        "COMPLETADO",
                                        "COMPLETADO"
                                )
                        )
                );

                kafkaTemplate.send(
                            "stock.respuesta",
                            ventaId.toString(),
                            respuesta
                    );
            }
        } catch (Exception e) {

            log.error("Error procesando mensaje Kafka: {}", record.value(), e);

            EventEnvelope respuesta = new EventEnvelope(
                    EventType.ERROR_STOCK,
                    objectMapper.valueToTree(
                            new StockRespuestaEvent(
                                    ventaId,
                                    "ERROR",
                                    "ERROR"
                            )
                    )
            );
            kafkaTemplate.send(
                    "stock.respuesta",
                    null,
                    respuesta
            );
        }
    }
}
