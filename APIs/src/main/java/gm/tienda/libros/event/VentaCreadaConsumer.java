package gm.tienda.libros.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import gm.events.common.EventEnvelope;
import gm.tienda.libros.service.imp.LibroService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class VentaCreadaConsumer {

    private final LibroService libroService;
    private final KafkaTemplate<String, StockRespuestaEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public VentaCreadaConsumer(LibroService libroService, KafkaTemplate<String, StockRespuestaEvent> kafkaTemplate,
                               ObjectMapper objectMapper) {
        this.libroService = libroService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "ventas.eventos")
    public void consumirEvento(EventEnvelope event) {

        if (event == null) {
            log.warn("Evento nulo recibido");
            return;
        }

        try {
            switch (event.eventType()) {

                case "VENTA_CREADA" -> {
                    VentaCreadaEvent venta =
                            objectMapper.convertValue(event.payload(), VentaCreadaEvent.class);

                    libroService.rebajarStock(venta.isbnLibro(), venta.cantidad());

                    kafkaTemplate.send(
                            "stock.respuesta",
                            venta.idVenta().toString(),
                            new StockRespuestaEvent(
                                    venta.idVenta(),
                                    "COMPLETADO",
                                    "Stock descontado correctamente"
                            )
                    );
                }

                default -> {
                    log.warn("Evento no soportado: {}", event.eventType());

                    kafkaTemplate.send(
                            "stock.respuesta",
                            null,
                            new StockRespuestaEvent(
                                    null,
                                    "ERROR",
                                    "Evento no soportado: " + event.eventType()
                            )
                    );
                }
            }

        } catch (Exception e) {

            log.error("Error procesando evento {}", event.eventType(), e);

            kafkaTemplate.send(
                    "stock.respuesta",
                    null,
                    new StockRespuestaEvent(
                            null,
                            "ERROR",
                            e.getMessage()
                    )
            );
        }
    }
}
