package gm.tienda_libros.controller;

import gm.tienda_libros.model.Cliente;
import gm.tienda_libros.service.imp.ClienteService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService){
        this.clienteService = clienteService;
    }

    @GetMapping()
    public ResponseEntity<List<Cliente>> getAll(){
        List<Cliente> clientes = clienteService.listarClientes();

        logger.info("Cantidad de clientes: {}", clientes.size());
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        Cliente clienteBuscado = clienteService.obtenerClientePorId(id);

        logger.info("Cliente con id: {}", id);
        return ResponseEntity.ok(clienteBuscado);
    }

    @PostMapping()
    public ResponseEntity<?> post(@RequestBody @Valid Cliente cliente){
        Cliente nuevoCLiente = clienteService.registrarCliente(cliente);

        logger.info("Cliente creado con id: {}", nuevoCLiente.getId());
        URI location = URI.create("/api/clientes/"+nuevoCLiente.getId());
        return ResponseEntity.created(location).body(nuevoCLiente);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> put(@PathVariable Integer id, @RequestBody @Valid Cliente cliente){
        Cliente clienteActualizado = clienteService.actualizarCliente(id,cliente);

        logger.info("Cliente actualizado con id: {}", clienteActualizado.getId());
        return ResponseEntity.ok(clienteActualizado);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id){
        clienteService.eliminarCliente(id);

        logger.info("Cliente eliminado con id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
