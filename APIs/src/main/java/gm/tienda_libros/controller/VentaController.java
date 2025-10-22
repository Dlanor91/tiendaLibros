package gm.tienda_libros.controller;

import gm.tienda_libros.DTOs.VentaDTO;
import gm.tienda_libros.model.Venta;
import gm.tienda_libros.service.imp.VentaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping()
    public ResponseEntity<List<VentaDTO>> getAll(){
        List<VentaDTO> ventas = ventaService.listarVentas();

        logger.info("Ventas listadas: {}",ventas.size());

        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> getByCodigo(@PathVariable String codigo){
        Venta ventaEncontrada = ventaService.obtenerVentaByCodigo(codigo);

        logger.info("Ventas encontrada con codigo: {}",codigo);

        return ResponseEntity.ok(ventaEncontrada);
    }

    @PostMapping("")
    public ResponseEntity<?> post(@RequestBody @Valid Venta venta){
        Venta nuevaVenta = ventaService.crearVenta(venta);

        logger.info("Venta generada correctamente con ID: {}", nuevaVenta.getId());

        URI location= URI.create("/api/ventas/"+ nuevaVenta.getId());
        return ResponseEntity.created(location).body(nuevaVenta);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<?> put(@PathVariable String codigo, @RequestBody @Valid Venta venta){
        Venta actualizarVenta = ventaService.actualizarVenta(codigo, venta);

        logger.info("Venta actualziada correctamente con codigo: {}", codigo);

        return ResponseEntity.ok(actualizarVenta);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(@PathVariable String codigo){
        ventaService.eliminarVenta(codigo);

        logger.info("Venta eliminada con codigo: {}",codigo);
        return ResponseEntity.noContent().build();
    }
}
