package gm.tienda.libros.service.imp;

import gm.tienda.libros.dto.AutorDTO;
import gm.tienda.libros.dto.LibroDTO;
import gm.tienda.libros.dto.LibroRequestDTO;
import gm.tienda.libros.model.Autor;
import gm.tienda.libros.model.Libro;
import gm.tienda.libros.repository.AutorRepository;
import gm.tienda.libros.repository.GeneroLiterarioRepository;
import gm.tienda.libros.repository.LibroRepository;
import gm.tienda.libros.service.ILibroService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class LibroService implements ILibroService {

    private static final String MSG_GENERO_NO_ENCONTRADO = "Género no encontrado";
    private static final String MSG_GENERO_NO_NULL = " no puede ser null";

    private final LibroRepository libroRepository;

    private final GeneroLiterarioRepository generoLiterarioRepository;

    private final AutorRepository autorRepository;

    public LibroService (LibroRepository libroRepository, GeneroLiterarioRepository generoLiterarioRepository,
                         AutorRepository autorRepository)
    {
        this.libroRepository = libroRepository;
        this.generoLiterarioRepository = generoLiterarioRepository;
        this.autorRepository = autorRepository;
    }

    @Override
    public List<LibroDTO> listarLibros() {
        return libroRepository.findAll(Sort.by("isbn"))
                .stream()
                .map(libro -> new LibroDTO(
                        libro.getIsbn(),
                        libro.getNombre(),
                        libro.getCodMoneda(),
                        libro.getPrecio(),
                        libro.getStock(),
                        libro.getDescripcion(),
                        libro.getFechaPublicacion(),
                        libro.getCodGeneroLiterario(),
                        libro.getGeneroLiterario() != null
                                ? libro.getGeneroLiterario().getNombre()
                                : null,
                        libro.getAutores() != null
                                ? libro.getAutores().stream()
                                .map(a -> new AutorDTO(
                                        a.getId(),
                                        a.getNombre(),
                                        a.getApellidos()
                                ))
                                .toList()
                                : List.of()
                ))
                .toList();
    }

    @Override
    public LibroDTO buscarLibroPorIsbn(String isbn) {
        validarCampo(isbn,"isbn");

        Libro libro = libroRepository.findByIsbn(isbn);
        if (libro == null) {
            throw new EntityNotFoundException("Libro con ISBN " + isbn + " no encontrado");
        }

        generoLiterarioRepository.findByCodigo(libro.getCodGeneroLiterario())
                .orElseThrow(() -> new EntityNotFoundException(MSG_GENERO_NO_ENCONTRADO));

        return mapToLibroDTO(libro);
    }

    @Override
    public LibroDTO insertarLibro(LibroRequestDTO libro) {
        Objects.requireNonNull(libro, "El libro " + libro + MSG_GENERO_NO_NULL);

        generoLiterarioRepository.findByCodigo(libro.codGeneroLiterario())
                .orElseThrow(() -> new EntityNotFoundException(MSG_GENERO_NO_ENCONTRADO));

        List<Autor> autores = autorRepository.findAllById(libro.autoresIds());

        Libro nuevoLibro = new Libro();

        nuevoLibro.setIsbn(libro.isbn());
        nuevoLibro.setNombre(libro.nombre());
        nuevoLibro.setCodMoneda(libro.codMoneda());
        nuevoLibro.setPrecio(libro.precio());
        nuevoLibro.setStock(libro.stock());
        nuevoLibro.setDescripcion(libro.descripcion());
        nuevoLibro.setFechaPublicacion(libro.fechaPublicacion());
        nuevoLibro.setCodGeneroLiterario(libro.codGeneroLiterario());
        nuevoLibro.setAutores(autores);

        Libro guardado = libroRepository.save(nuevoLibro);
        return mapToLibroDTO(guardado);
    }

    @Override
    public LibroDTO  actualizarLibro(String isbn, LibroRequestDTO libro) {
        Objects.requireNonNull(libro, "El libro " + libro + MSG_GENERO_NO_NULL);

        validarCampo(isbn, "isbn");

        generoLiterarioRepository.findByCodigo(libro.codGeneroLiterario())
                .orElseThrow(() -> new EntityNotFoundException(MSG_GENERO_NO_ENCONTRADO));

        Libro actualizarLibro = obtenerLibroEntidad(isbn);

        actualizarLibro.setNombre(libro.nombre());
        actualizarLibro.setCodMoneda(libro.codMoneda());
        actualizarLibro.setPrecio(libro.precio());
        actualizarLibro.setStock(libro.stock());
        actualizarLibro.setDescripcion(libro.descripcion());
        actualizarLibro.setFechaPublicacion(libro.fechaPublicacion());

        Libro guardado = libroRepository.save(actualizarLibro);
        return mapToLibroDTO(guardado);
    }

    @Override
    public void eliminarLibro(String isbn) {
        validarCampo(isbn,"isbn");

        Libro libroEliminar = libroRepository.findByIsbn(isbn);
        if (libroEliminar == null) {
            throw new EntityNotFoundException("Libro con ISBN " + isbn + " no encontrado");
        }

        libroRepository.delete(libroEliminar);
    }

    @Override
    public List<LibroDTO> buscarLibrosCodGeneroLiterario(String codGenero) {
        validarCampo(codGenero,"codGenero");

        return libroRepository.findByCodGeneroLiterario(codGenero)
                .stream()
                .map(libro -> new LibroDTO(
                        libro.getIsbn(),
                        libro.getNombre(),
                        libro.getCodMoneda(),
                        libro.getPrecio(),
                        libro.getStock(),
                        libro.getDescripcion(),
                        libro.getFechaPublicacion(),
                        libro.getCodGeneroLiterario(),
                        libro.getGeneroLiterario() != null
                                ? libro.getGeneroLiterario().getNombre()
                                : null,
                        libro.getAutores() != null
                                ? libro.getAutores().stream()
                                .map(a -> new AutorDTO(
                                        a.getId(),
                                        a.getNombre(),
                                        a.getApellidos()
                                ))
                                .toList()
                                : List.of()
                ))
                .toList();
    }

    @Override
    public void rebajarStock(String isbn, Integer cantidad) {

        Libro libro = obtenerLibroEntidad(isbn);

        libro.setStock(libro.getStock()-cantidad);

        libroRepository.save(libro);
    }

    private Libro obtenerLibroEntidad(String isbn) {
        validarCampo(isbn, "isbn");
        return libroRepository.findByIsbn(isbn);
    }

    private void validarCampo(String valor, String nombreCampo) {
        Objects.requireNonNull(valor, "El campo " + nombreCampo + MSG_GENERO_NO_NULL);

        if (valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + nombreCampo + " no puede estar vacío");
        }
    }

    private LibroDTO mapToLibroDTO(Libro libro) {

        String nombreGenero = libro.getGeneroLiterario() != null
                ? libro.getGeneroLiterario().getNombre()
                : null;

        List<AutorDTO> autoresDto = libro.getAutores() != null
                ? libro.getAutores().stream()
                .map(a -> new AutorDTO(a.getId(), a.getNombre(), a.getApellidos()))
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
                nombreGenero,
                autoresDto
        );
    }
}
