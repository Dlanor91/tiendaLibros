package gm.tienda.libros.scheduler;

import gm.tienda.libros.config.MsVentasProperties;
import gm.tienda.libros.dto.VentaMLDTO;
import gm.tienda.libros.service.imp.LibroService;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class VentaPollingScheduler {
    private final RestTemplate restTemplate;
    private final LibroService libroService;
    private final MsVentasProperties props;

    public VentaPollingScheduler(RestTemplate restTemplate, LibroService libroService, MsVentasProperties props) {
        this.restTemplate = restTemplate;
        this.libroService = libroService;
        this.props = props;
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void procesarVentas() {

        String ventasServiceUrl = props.getUrl();

        VentaMLDTO[] ventas = restTemplate.getForObject(ventasServiceUrl + "/sinProcesar", VentaMLDTO[].class);

        if (ventas == null || ventas.length == 0) {
            return;
        }

        Arrays.stream(ventas).forEach(venta -> {
            libroService.rebajarStock(venta.isbnLibro(), venta.cantidad());

            // Marcar venta como procesada en el MS Venta
            restTemplate.postForObject(
                ventasServiceUrl + "/" + venta.id() + "/procesar",
                HttpEntity.EMPTY,
                Void.class
            );
        });
    }
}
