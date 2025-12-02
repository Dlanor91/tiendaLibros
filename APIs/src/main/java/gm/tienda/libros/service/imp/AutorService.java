package gm.tienda.libros.service.imp;

import gm.tienda.libros.dto.AutorDTO;
import gm.tienda.libros.dto.AutorDetalleDTO;
import gm.tienda.libros.dto.AutorRequestDTO;
import gm.tienda.libros.dto.LibroDTO;
import gm.tienda.libros.model.Autor;
import gm.tienda.libros.model.Libro;
import gm.tienda.libros.repository.AutorRepository;
import gm.tienda.libros.service.IAutorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AutorService implements IAutorService {
    private final AutorRepository  autorRepository;

    public AutorService (AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    @Override
    public List<AutorDTO> listarAutores() {
        return autorRepository.findAll(Sort.by("nombre"))
                .stream()
                .map(autor -> new AutorDTO(
                        autor.getId(),
                        autor.getNombre(),
                        autor.getApellidos()
                ))
                .toList();
    }

    @Override
    public AutorDetalleDTO obtenerAutorPorId(Integer id) {
        Objects.requireNonNull(id, "El id no puede ser null");

        if(id <= 0){
            throw new IllegalArgumentException("El id debe ser mayor que 0");
        }

        Autor autor = findAutorOrThrow(id);

        return mapToAutorDetalleDTO(autor);
    }

    @Override
    public Autor crearAutor(AutorRequestDTO autor) {
        Objects.requireNonNull(autor, "El autor no puede ser null");

        Autor autorNuevo = new Autor();
        autorNuevo.setNombre(autor.nombre());
        autorNuevo.setApellidos(autor.apellidos());

        return autorRepository.save(autorNuevo);
    }

    @Override
    public Autor actualizarAutor(Integer id, AutorRequestDTO autor) {
        Objects.requireNonNull(autor, "El autor no puede ser null");

        Autor autorExistente = findAutorOrThrow(id);

        //Modifico el resto de campos
        autorExistente.setNombre(autor.nombre());
        autorExistente.setApellidos(autor.apellidos());

        return autorRepository.save(autorExistente);
    }

    @Override
    public void eliminarAutor(Integer id) {
        Objects.requireNonNull(id, "El id no puede ser null");

        if(id <= 0){
            throw new IllegalArgumentException("El id no puede ser menor igual a 0");
        }

        Autor autorEliminar = findAutorOrThrow(id);

        autorRepository.delete(autorEliminar);
    }

    @Override
    public List<Autor> buscarAutoresNombre(String nombre) {
         validarCampo(nombre,"nombre");

        return autorRepository.findByNombreContainingIgnoreCaseOrderByNombre(nombre);
    }

    private Autor findAutorOrThrow(Integer id) {
        if(id <= 0){
            throw new IllegalArgumentException("El id no puede ser menor igual a 0");
        }

        return autorRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("No se encontró el autor con id: " + id));
    }

    private AutorDetalleDTO mapToAutorDetalleDTO(Autor autor) {

        List<LibroDTO> libros = autor.getLibros()
                .stream()
                .map(this::mapToLibroDTO)
                .toList();

        return new AutorDetalleDTO(
                autor.getId(),
                autor.getNombre(),
                autor.getApellidos(),
                libros
        );
    }

    private LibroDTO mapToLibroDTO(Libro libro) {

        String genero = libro.getGeneroLiterario() != null
                ? libro.getGeneroLiterario().getNombre()
                : null;

        List<AutorDTO> autores = libro.getAutores() != null
                ? libro.getAutores().stream()
                .map(a -> new AutorDTO(
                        a.getId(),
                        a.getNombre(),
                        a.getApellidos()
                ))
                .toList()
                : List.of();

        return new LibroDTO(
                libro.getIsbn(),
                libro.getNombre(),
                libro.getCodMoneda(),
                libro.getPrecio(),
                libro.getStock(),
                libro.getDescripcion(),
                libro.getFechaPublicacion(),
                libro.getCodGeneroLiterario(),
                genero,
                autores
        );
    }

    private void validarCampo(String valor, String nombreCampo) {
        Objects.requireNonNull(valor, "El campo " + nombreCampo + " no puede ser null");

        if (valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + nombreCampo + " no puede estar vacío");
        }
    }
}
