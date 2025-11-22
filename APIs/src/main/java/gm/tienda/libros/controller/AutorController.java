package gm.tienda.libros.controller;

import gm.tienda.libros.dto.AutorDTO;
import gm.tienda.libros.dto.AutorDetalleDTO;
import gm.tienda.libros.dto.AutorRequestDTO;
import gm.tienda.libros.model.Autor;
import gm.tienda.libros.service.imp.AutorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/autores")
public class AutorController {

    private static final Logger logger = LoggerFactory.getLogger(AutorController.class);

    private final AutorService autorService;

    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    @GetMapping()
    public ResponseEntity<List<AutorDTO>> getAll(){
        List<AutorDTO> autores = autorService.listarAutores();

        logger.info("Cantidad de autores: {}", autores.size());
        return ResponseEntity.ok(autores);
    }

    @GetMapping("{id}")
    public ResponseEntity<AutorDetalleDTO> getById(@PathVariable Integer id){
        AutorDetalleDTO autorEncontrado = autorService.obtenerAutorPorId(id);

        logger.info("Autor encontrado con id: {}", autorEncontrado.id());
        return ResponseEntity.ok(autorEncontrado);
    }

    @PostMapping()
    public ResponseEntity<Autor> post (@RequestBody @Valid AutorRequestDTO autor){
        Autor autorNuevo = autorService.crearAutor(autor);

        logger.info("Autor creado con id: {}", autorNuevo.getId());
        URI location = URI.create("/api/autores/"+autorNuevo.getId());
        return ResponseEntity.created(location).body(autorNuevo);
    }

    @PutMapping("{id}")
    public ResponseEntity<Autor> put(@PathVariable Integer id, @RequestBody @Valid AutorRequestDTO autor){
        Autor autorModificado = autorService.actualizarAutor(id, autor);

        logger.info("Autor actualizado con id: {}", autorModificado.getId());
        return ResponseEntity.ok(autorModificado);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id){
        autorService.eliminarAutor(id);

        logger.info("Autor eliminado con id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Autor>> getAllAutoresByNombre(@RequestParam String nombre)
    {
        List<Autor> autoresEncontrados = autorService.buscarAutoresNombre(nombre);

        logger.info("Cantidad de autores encontrados: {} con el filtro de nombre:{} ", autoresEncontrados.size(), nombre);
        return ResponseEntity.ok(autoresEncontrados);
    }
}
