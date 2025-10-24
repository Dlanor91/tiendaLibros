package gm.tienda_libros.controller;

import gm.tienda_libros.model.GeneroLiterario;
import gm.tienda_libros.service.imp.GeneroLiterarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

    @GetMapping("/{codigo}")
    public ResponseEntity<?> getByCodigo(@PathVariable String codigo){
        GeneroLiterario generoLiterario = generoLiterarioService.obtenerGeneroLiterarioByCodigo(codigo);

        logger.info("Genero literario encontrado con codigo: {}", codigo);

        return ResponseEntity.ok(generoLiterario);
    }

    @PostMapping()
    public ResponseEntity<?> post(@RequestBody @Valid GeneroLiterario generoLiterario){
        GeneroLiterario generoLiterarioNuevo = generoLiterarioService.agregarGeneroLiterario(generoLiterario);

        logger.info("Genero literario generado con codigo: {}", generoLiterarioNuevo.getCodigo());
        URI location = URI.create("/api/generosLiterarios/"+generoLiterarioNuevo.getCodigo());

        return ResponseEntity.created(location).body(generoLiterarioNuevo);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<?> put(@PathVariable String codigo, @RequestBody @Valid GeneroLiterario generoLiterario){
        GeneroLiterario generoLiterarioActualziar = generoLiterarioService.actualizarGeneroLiterario(codigo, generoLiterario);

        logger.info("Genero literario modificado con codigo: {}", codigo);

        return  ResponseEntity.ok(generoLiterario);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(@PathVariable String codigo){
        generoLiterarioService.eliminarGeneroLiterario(codigo);

        logger.info("Genero literario eliminado con codigo: {}", codigo);

        return ResponseEntity.noContent().build();
    }
}
