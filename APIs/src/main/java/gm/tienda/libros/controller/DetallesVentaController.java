package gm.tienda.libros.controller;

import gm.tienda.libros.dto.DetallesVentaDTO;
import gm.tienda.libros.model.DetallesVenta;
import gm.tienda.libros.service.imp.DetallesVentaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/detallesVenta")
public class DetallesVentaController {

    private final DetallesVentaService detallesVentaService;

    public DetallesVentaController(DetallesVentaService detallesVentaService){
        this.detallesVentaService = detallesVentaService;
    }

    @GetMapping
    public ResponseEntity<List<DetallesVentaDTO>> getAll() {
        return ResponseEntity.ok(detallesVentaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallesVentaDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(detallesVentaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<DetallesVenta> post(@RequestBody @Valid DetallesVenta dv) {
        DetallesVenta nuevo = detallesVentaService.crear(dv);
        return ResponseEntity.created(URI.create("/api/detalles-venta/" + nuevo.getId()))
                .body(nuevo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        detallesVentaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
