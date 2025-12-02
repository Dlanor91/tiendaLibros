package gm.tienda.libros.controller;

import gm.tienda.libros.dto.LibroDTO;
import gm.tienda.libros.dto.LibroRequestDTO;
import gm.tienda.libros.service.imp.LibroService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private static final Logger logger = LoggerFactory.getLogger(LibroController.class);

    private final LibroService libroService;

    public LibroController(LibroService libroService){
        this.libroService = libroService;
    }

    @GetMapping()
    ResponseEntity<List<LibroDTO>> getAll(){
        List<LibroDTO> libros = libroService.listarLibros();

        logger.info("Cantidad de libros: {}", libros.size());
        return ResponseEntity.ok(libros);
    }

    @GetMapping("{isbn}")
    ResponseEntity<LibroDTO> getByIsbn(@PathVariable String isbn){
        LibroDTO libro = libroService.buscarLibroPorIsbn(isbn);

        logger.info("Libro encontrado con isbn: {}", isbn);
        return ResponseEntity.ok(libro);
    }

    @PostMapping()
    ResponseEntity<LibroDTO> post(@RequestBody @Valid LibroRequestDTO libroDto){
        LibroDTO libroNuevo = libroService.insertarLibro(libroDto);

        logger.info("Libro creado con isbn: {}", libroNuevo.isbn());
        URI location = URI.create("/api/libros/"+libroNuevo.isbn());
        return ResponseEntity.created(location).body(libroNuevo);
    }

    @PutMapping("{isbn}")
    ResponseEntity<LibroDTO> put(@PathVariable String isbn, @RequestBody @Valid LibroRequestDTO libroDto){
        LibroDTO libroEditado = libroService.actualizarLibro(isbn,libroDto);

        logger.info("Libro editado con isbn: {}", libroEditado.isbn());
        return  ResponseEntity.ok(libroEditado);
    }

    @DeleteMapping("{isbn}")
    ResponseEntity<Void> delete(@PathVariable String isbn){
        libroService.eliminarLibro(isbn);

        logger.info("Se elimino el libro con isbn: {}", isbn);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    ResponseEntity<List<LibroDTO>> buscarLibrosPorGenerp (@RequestParam String genero){
        List<LibroDTO> libros = libroService.buscarLibrosCodGeneroLiterario(genero);

        logger.info("Cantidad de libros encontrados: {} con el codigo de genero literario: {}", libros.size(), genero);
        return ResponseEntity.ok(libros);
    }
}
