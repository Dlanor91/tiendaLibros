package gm.tienda_libros.controller;

import gm.tienda_libros.DTOs.VentaDTO;
import gm.tienda_libros.model.Venta;
import gm.tienda_libros.service.imp.VentaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);

    @Autowired
    private VentaService ventaService;

    @GetMapping()
    public ResponseEntity<List<VentaDTO>> getAll(){
        List<VentaDTO> ventas = ventaService.listarVentas();

        logger.info("Ventas listadas: {}",ventas.size());

        return ResponseEntity.ok(ventas);
    }

    @PostMapping("")
    public ResponseEntity<?> post(@RequestBody @Valid Venta venta){
        Venta nuevaVenta = ventaService.crearVenta(venta);

        logger.info("Ventas generada correctamente con ID: {}",nuevaVenta.getId());

        URI location= URI.create("/api/ventas/"+ nuevaVenta.getId());
        return ResponseEntity.created(location).body(nuevaVenta);
    }
}
