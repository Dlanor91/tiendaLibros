package gm.tienda.libros.service;

import gm.tienda.libros.dto.LibroDTO;
import gm.tienda.libros.model.GeneroLiterario;
import gm.tienda.libros.model.Libro;
import gm.tienda.libros.repository.GeneroLiterarioRepository;
import gm.tienda.libros.repository.LibroRepository;
import gm.tienda.libros.service.imp.LibroService;
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

    private LibroDTO crearLibroDto() {
        return new LibroDTO(
                "ISBN123",
                "Nombre",
                "USD",
                new BigDecimal("10"),
                5,
                "Desc",
                LocalDate.now(),
                "FIC",
                "Ficción",
                List.of()
        );
    }

    // ===============================================================
    //                           LISTAR
    // ===============================================================

    @Test
    @DisplayName("Debe listar todos los libros correctamente")
    void listarLibros_DebeRetornarLista() {

        // --- Crear genero ---
        GeneroLiterario genero = new GeneroLiterario();
        genero.setCodigo("FIC");
        genero.setNombre("Ficción");

        // --- Crear libro mock ---
        Libro libroMock = crearLibro();
        libroMock.setGeneroLiterario(genero);

        // --- Mock correcto de findAll(Sort) ---
        when(libroRepository.findAll(Sort.by("isbn")))
                .thenReturn(List.of(libroMock));

        // --- Ejecutar servicio ---
        List<LibroDTO> resultado = libroService.listarLibros();

        // --- Asserts ---
        assertThat(resultado)
                .isNotNull()
                .hasSize(1);

        LibroDTO dto = resultado.get(0);

        assertThat(dto.isbn()).isEqualTo("TEST-123");
        assertThat(dto.nombre()).isEqualTo("Nombre Test");
        assertThat(dto.codGeneroLiterario()).isEqualTo("FIC");
        assertThat(dto.nombreGeneroLiterario()).isEqualTo("Ficción");

        // --- Verificación exacta ---
        verify(libroRepository).findAll(Sort.by("isbn"));
        verifyNoMoreInteractions(libroRepository);
    }


    // ===============================================================
    //                       BUSCAR POR ISBN
    // ===============================================================
    @Test
    @DisplayName("Debe obtener un libro por su ISBN")
    void buscarPorIsbn_DebeRetornarLibro() {

        // ARRANGE
        Libro entidad = crearLibro();
        entidad.setIsbn("TEST-123");
        entidad.setCodGeneroLiterario("FIC");

        GeneroLiterario genero = new GeneroLiterario();
        genero.setCodigo("FIC");
        genero.setNombre("Ficción");

        when(libroRepository.findByIsbn("TEST-123"))
                .thenReturn(entidad);

        when(generoRepository.findByCodigo("FIC"))
                .thenReturn(Optional.of(genero));

        // ACT
        LibroDTO resultado = libroService.buscarLibroPorIsbn("TEST-123");

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.isbn()).isEqualTo("TEST-123");
        assertThat(resultado.nombreGeneroLiterario()).isEqualTo("Ficción");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el libro no existe al buscar ISBN")
    void buscarPorIsbn_NoExiste_DebeLanzarExcepcion() {
        when(libroRepository.findByIsbn("XYZ")).thenReturn(null);

        assertThatThrownBy(() -> libroService.buscarLibroPorIsbn("XYZ"))
                .isInstanceOf(RuntimeException.class)
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

    /*@Test
    @DisplayName("Debe insertar un libro si el ISBN no existe")
    void insertarLibro_DebeGuardar() {
        LibroDTO dto = crearLibroDto();
        Libro entidad = crearLibro();
        GeneroLiterario genero = new GeneroLiterario("Ficción", "FIC");

        when(libroRepository.existsByIsbn("TEST-123")).thenReturn(false);
        when(generoRepository.findByCodigo("FIC")).thenReturn(Optional.of(genero));
        when(libroRepository.save(any(Libro.class))).thenReturn(entidad);

        Libro guardado = libroService.insertarLibro(dto);

        assertThat(guardado.getIsbn()).isEqualTo("TEST-123");
        verify(libroRepository).save(any(Libro.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el ISBN ya existe al insertar")
    void insertarLibro_IsbnExiste_DebeLanzarExcepcion() {
        LibroDTO dto = crearLibroDto();
        when(libroRepository.existsByIsbn("TEST-123")).thenReturn(true);

        assertThatThrownBy(() -> libroService.insertarLibro(dto))
                .isInstanceOf(EntityExistsException.class)
                .hasMessageContaining("Ya existe");
    }*/

    // ===============================================================
    //                           UPDATE
    // ===============================================================

    /*@Test
    @DisplayName("Debe actualizar un libro existente")
    void actualizarLibro_DebeActualizar() {
        Libro existente = crearLibro();
        LibroDTO datosActualizados = crearLibroDto();

        when(libroRepository.findByIsbn("TEST-123")).thenReturn(existente);
        when(libroRepository.save(existente)).thenReturn(existente);

        Libro actualizado = libroService.actualizarLibro("TEST-123", datosActualizados);

        assertThat(actualizado.getNombre()).isEqualTo(datosActualizados.nombre());
        verify(libroRepository).save(existente);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar libro inexistente")
    void actualizarLibro_NoExiste_DebeLanzarExcepcion() {

        when(libroRepository.findByIsbn("XYZ")).thenReturn(null);

        // ✔ Crear el DTO fuera del lambda
        LibroDTO dto = crearLibroDto();

        assertThatThrownBy(() -> libroService.actualizarLibro("XYZ", dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No encontrado");
    }*/

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
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no encontrado");
    }

    // ===============================================================
    //                     BUSCAR POR CÓDIGO GÉNERO
    // ===============================================================

    @Test
    @DisplayName("Debe buscar libros por código de género literario")
    void buscarPorGenero_DebeRetornarLista() {
        Libro libro = crearLibro(); // AHORA sí tiene género

        when(libroRepository.findByCodGeneroLiterario("FIC"))
                .thenReturn(List.of(libro));

        List<LibroDTO> resultado = libroService.buscarLibrosCodGeneroLiterario("FIC");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).codGeneroLiterario()).isEqualTo("FIC");
        assertThat(resultado.get(0).nombreGeneroLiterario()).isEqualTo("Ficción");
    }
}
