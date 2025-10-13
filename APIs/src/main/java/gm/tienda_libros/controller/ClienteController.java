package gm.tienda_libros.controller;

import gm.tienda_libros.model.Cliente;
import gm.tienda_libros.service.imp.ClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    @Autowired
    private ClienteService clienteService;

    @GetMapping()
    public ResponseEntity<List<Cliente>> getAll(){
        List<Cliente> clientes = clienteService.listarClientes();

        logger.info("Cantidad de clientes: {}", clientes.size());
        return ResponseEntity.ok(clientes) ;
    }
}
