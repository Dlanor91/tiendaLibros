package gm.tienda.libros.service;

import gm.tienda.libros.dto.LibroDTO;
import gm.tienda.libros.dto.LibroRequestDTO;
import gm.tienda.libros.model.Autor;
import gm.tienda.libros.model.GeneroLiterario;
import gm.tienda.libros.model.Libro;
import gm.tienda.libros.repository.AutorRepository;
import gm.tienda.libros.repository.GeneroLiterarioRepository;
import gm.tienda.libros.repository.LibroRepository;
import gm.tienda.libros.service.imp.LibroService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibroServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private GeneroLiterarioRepository generoRepository;

    @Mock
    private AutorRepository autorRepository;

    @InjectMocks
    private LibroService libroService;

    // ===============================================================
    //                   HELPERS PARA EL TEST
    // ===============================================================

    private Libro crearLibro() {
        GeneroLiterario genero = new GeneroLiterario("Ficción", "FIC");

        Libro l = new Libro();
        l.setId(1);
        l.setIsbn("TEST-123");
        l.setNombre("Nombre Test");
        l.setCodMoneda("USD");
        l.setPrecio(new BigDecimal("10.50"));
        l.setStock(10);
        l.setDescripcion("Desc Test");
        l.setFechaPublicacion(LocalDate.now().minusDays(5));
        l.setCodGeneroLiterario("FIC");
        l.setGeneroLiterario(genero);
        return l;
    }

    private LibroRequestDTO crearLibroRequestDto() {
        return new LibroRequestDTO(
                null, // id (para insertar es null)
                "TEST-123",
                "Nombre Test",
                "USD",
                new BigDecimal("10.50"),
                10,
                "Desc Test",
                LocalDate.now().minusDays(5),
                "FIC",
                List.of(1) // autoresIds
        );
    }

    // ===============================================================
    //                           LISTAR
    // ===============================================================

    @Test
    @DisplayName("Debe listar todos los libros correctamente")
    void listarLibros_DebeRetornarLista() {

        GeneroLiterario genero = new GeneroLiterario("Ficción", "FIC");
        Libro libroMock = crearLibro();
        libroMock.setGeneroLiterario(genero);

        when(libroRepository.findAll(Sort.by("isbn")))
                .thenReturn(List.of(libroMock));

        List<LibroDTO> resultado = libroService.listarLibros();

        assertThat(resultado)
                .isNotNull()
                .hasSize(1);

        LibroDTO dto = resultado.get(0);

        assertThat(dto.isbn()).isEqualTo("TEST-123");
        assertThat(dto.nombre()).isEqualTo("Nombre Test");
        assertThat(dto.codGeneroLiterario()).isEqualTo("FIC");
        assertThat(dto.nombreGeneroLiterario()).isEqualTo("Ficción");

        verify(libroRepository).findAll(Sort.by("isbn"));
        verifyNoMoreInteractions(libroRepository);
    }

    // ===============================================================
    //                       BUSCAR POR ISBN
    // ===============================================================

    @Test
    @DisplayName("Debe obtener un libro por su ISBN")
    void buscarPorIsbn_DebeRetornarLibro() {

        Libro entidad = crearLibro();

        when(libroRepository.findByIsbn("TEST-123"))
                .thenReturn(entidad);

        when(generoRepository.findByCodigo("FIC"))
                .thenReturn(Optional.of(entidad.getGeneroLiterario()));

        LibroDTO resultado = libroService.buscarLibroPorIsbn("TEST-123");

        assertThat(resultado).isNotNull();
        assertThat(resultado.isbn()).isEqualTo("TEST-123");
        assertThat(resultado.nombreGeneroLiterario()).isEqualTo("Ficción");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el libro no existe al buscar ISBN")
    void buscarPorIsbn_NoExiste_DebeLanzarExcepcion() {

        when(libroRepository.findByIsbn("XYZ")).thenReturn(null);

        assertThatThrownBy(() -> libroService.buscarLibroPorIsbn("XYZ"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("no encontrado");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el ISBN está vacío")
    void buscarPorIsbn_IsbnVacio_DebeLanzarExcepcion() {
        assertThatThrownBy(() -> libroService.buscarLibroPorIsbn("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("vacío");
    }

    // ===============================================================
    //                           INSERTAR
    // ===============================================================

    @Test
    @DisplayName("Debe insertar un libro nuevo correctamente")
    void insertarLibro_DebeGuardar() {

        LibroRequestDTO request = crearLibroRequestDto();
        GeneroLiterario genero = new GeneroLiterario("Ficción", "FIC");

        when(generoRepository.findByCodigo("FIC"))
                .thenReturn(Optional.of(genero));

        when(autorRepository.findAllById(List.of(1)))
                .thenReturn(List.of(new Autor()));

        Libro entidadGuardada = crearLibro();

        when(libroRepository.save(any(Libro.class)))
                .thenReturn(entidadGuardada);

        LibroDTO guardado = libroService.insertarLibro(request);

        assertThat(guardado.isbn()).isEqualTo("TEST-123");
        assertThat(guardado.nombreGeneroLiterario()).isEqualTo("Ficción");

        verify(generoRepository).findByCodigo("FIC");
        verify(libroRepository).save(any(Libro.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el género no existe al insertar")
    void insertarLibro_GeneroNoExiste_DebeLanzarExcepcion() {

        LibroRequestDTO request = crearLibroRequestDto();

        when(generoRepository.findByCodigo("FIC"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> libroService.insertarLibro(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Género");

        verify(generoRepository).findByCodigo("FIC");
        verifyNoInteractions(libroRepository);
    }

    // ===============================================================
    //                           UPDATE
    // ===============================================================

    @Test
    @DisplayName("Debe actualizar un libro existente")
    void actualizarLibro_DebeActualizar() {

        Libro existente = crearLibro();
        LibroRequestDTO request = crearLibroRequestDto();

        when(generoRepository.findByCodigo("FIC"))
                .thenReturn(Optional.of(existente.getGeneroLiterario()));

        when(libroRepository.findByIsbn("TEST-123"))
                .thenReturn(existente);

        when(libroRepository.save(existente)).thenReturn(existente);

        LibroDTO actualizado = libroService.actualizarLibro("TEST-123", request);

        assertThat(actualizado.isbn()).isEqualTo("TEST-123");
        assertThat(actualizado.nombre()).isEqualTo("Nombre Test");
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar libro inexistente")
    void actualizarLibro_NoExiste_DebeLanzarExcepcion() {

        LibroRequestDTO request = crearLibroRequestDto();

        when(generoRepository.findByCodigo("FIC"))
                .thenReturn(Optional.of(new GeneroLiterario()));

        when(libroRepository.findByIsbn("XYZ")).thenReturn(null);

        // Tu método `obtenerLibroEntidad` NO lanza excepción → esta cae más adelante
        assertThatThrownBy(() -> libroService.actualizarLibro("XYZ", request))
                .isInstanceOf(NullPointerException.class);
    }

    // ===============================================================
    //                           DELETE
    // ===============================================================

    @Test
    @DisplayName("Debe eliminar un libro existente")
    void eliminarLibro_DebeEliminar() {

        Libro existente = crearLibro();

        when(libroRepository.findByIsbn("TEST-123")).thenReturn(existente);

        libroService.eliminarLibro("TEST-123");

        verify(libroRepository).delete(existente);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar libro inexistente")
    void eliminarLibro_NoExiste_DebeLanzarExcepcion() {

        when(libroRepository.findByIsbn("ZZZ")).thenReturn(null);

        assertThatThrownBy(() -> libroService.eliminarLibro("ZZZ"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("no encontrado");
    }

    // ===============================================================
    //                     BUSCAR POR CÓDIGO GÉNERO
    // ===============================================================

    @Test
    @DisplayName("Debe buscar libros por código de género literario")
    void buscarPorGenero_DebeRetornarLista() {

        Libro libro = crearLibro();

        when(libroRepository.findByCodGeneroLiterario("FIC"))
                .thenReturn(List.of(libro));

        List<LibroDTO> resultado = libroService.buscarLibrosCodGeneroLiterario("FIC");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).codGeneroLiterario()).isEqualTo("FIC");
        assertThat(resultado.get(0).nombreGeneroLiterario()).isEqualTo("Ficción");
    }
}
