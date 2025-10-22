package gm.tienda_libros.controller;

import gm.tienda_libros.model.GeneroLiterario;
import gm.tienda_libros.service.imp.GeneroLiterarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/generosLiterarios")
public class GeneroLiterarioController {

    private static final Logger logger = LoggerFactory.getLogger(GeneroLiterarioController.class);

    private final GeneroLiterarioService generoLiterarioService;

    public GeneroLiterarioController(GeneroLiterarioService generoLiterarioService){
        this.generoLiterarioService = generoLiterarioService;
    }

    @GetMapping()
    public ResponseEntity<List<GeneroLiterario>> getAll(){
        List<GeneroLiterario> generosLitearios = generoLiterarioService.listarGenerosLiterarios();

        logger.info("Listado de generos literarios, cantidad: {}", generosLitearios.size());

        return ResponseEntity.ok(generosLitearios);
    }
}
