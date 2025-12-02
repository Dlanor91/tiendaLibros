package gm.tienda.libros.repository;

import gm.tienda.libros.model.GeneroLiterario;
import gm.tienda.libros.model.Libro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class LibroRepositoryTest {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private GeneroLiterarioRepository generoLiterarioRepository;

    private Libro libroBase;

    @BeforeEach
    void setUp() {
        // Primero: insertar géneros literarios obligatorios
        generoLiterarioRepository.save(new GeneroLiterario("Ficción", "FIC"));
        generoLiterarioRepository.save(new GeneroLiterario("Drama", "DRA"));

        libroBase = new Libro();
        libroBase.setIsbn("TEST-123");
        libroBase.setNombre("Libro de prueba");
        libroBase.setCodMoneda("USD");
        libroBase.setPrecio(new BigDecimal("10.50"));
        libroBase.setStock(5);
        libroBase.setDescripcion("Descripción de prueba");
        libroBase.setFechaPublicacion(LocalDate.now().minusDays(5));
        libroBase.setCodGeneroLiterario("FIC");
    }

    private Libro guardarLibroBase() {
        return libroRepository.save(libroBase);
    }

    // --------------------------------------------------------------------------------
    // TESTS
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("Guardar un libro debe persistirlo correctamente")
    void guardarLibro_DebePersistir() {
        Libro guardado = guardarLibroBase();

        assertNotNull(guardado.getId());
        assertEquals("TEST-123", guardado.getIsbn());
        assertEquals("FIC", guardado.getCodGeneroLiterario());
        assertNotNull(guardado.getFechaPublicacion());
    }

    @Test
    @DisplayName("Buscar por ISBN debe retornar el libro correcto")
    void buscarPorIsbn_DebeRetornarLibro() {
        guardarLibroBase();

        Libro resultado = libroRepository.findByIsbn("TEST-123");

        assertNotNull(resultado);
        assertEquals("TEST-123", resultado.getIsbn());
        assertEquals("FIC", resultado.getCodGeneroLiterario());
    }

    @Test
    @DisplayName("existsByIsbn debe retornar true si el libro existe")
    void existsByIsbn_TrueCuandoExiste() {
        guardarLibroBase();

        boolean existe = libroRepository.existsByIsbn("TEST-123");

        assertTrue(existe);
    }

    @Test
    @DisplayName("existsByIsbn debe retornar false cuando no existe")
    void existsByIsbn_FalseCuandoNoExiste() {
        assertFalse(libroRepository.existsByIsbn("NO-EXISTE"));
    }

    @Test
    @DisplayName("findByCodGeneroLiterario debe retornar los libros del género indicado")
    void buscarPorGeneroLiterario_DebeRetornarListaCorrecta() {
        guardarLibroBase(); // FIC

        Libro drama = new Libro();
        drama.setIsbn("DRAMA-1");
        drama.setNombre("Libro Drama");
        drama.setCodMoneda("USD");
        drama.setPrecio(new BigDecimal("20"));
        drama.setStock(3);
        drama.setDescripcion("Desc drama");
        drama.setFechaPublicacion(LocalDate.now().minusDays(10));
        drama.setCodGeneroLiterario("DRA");

        libroRepository.save(drama);

        List<Libro> ficcion = libroRepository.findByCodGeneroLiterario("FIC");
        List<Libro> dramaLista = libroRepository.findByCodGeneroLiterario("DRA");

        assertEquals(1, ficcion.size());
        assertEquals("TEST-123", ficcion.get(0).getIsbn());

        assertEquals(1, dramaLista.size());
        assertEquals("DRAMA-1", dramaLista.get(0).getIsbn());
    }

    @Test
    @DisplayName("findAll debe retornar todos los libros guardados")
    void findAll_DebeRetornarTodos() {
        guardarLibroBase();

        Libro otro = new Libro();
        otro.setIsbn("OTRO-999");
        otro.setNombre("Segundo libro");
        otro.setCodMoneda("USD");
        otro.setPrecio(new BigDecimal("15"));
        otro.setStock(7);
        otro.setDescripcion("Otro libro");
        otro.setFechaPublicacion(LocalDate.now().minusDays(2));
        otro.setCodGeneroLiterario("FIC");

        libroRepository.save(otro);

        List<Libro> todos = libroRepository.findAll();

        assertEquals(2, todos.size());
    }

    @Test
    @DisplayName("Eliminar un libro debe removerlo de la base")
    void eliminarLibro_DebeEliminar() {
        Libro guardado = guardarLibroBase();

        libroRepository.deleteById(guardado.getId());

        Optional<Libro> resultado = libroRepository.findById(guardado.getId());

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Actualizar un libro debe persistir los cambios")
    void actualizarLibro_DebeActualizar() {
        Libro guardado = guardarLibroBase();

        guardado.setNombre("Nombre Actualizado");
        guardado.setStock(99);

        Libro actualizado = libroRepository.save(guardado);

        assertEquals("Nombre Actualizado", actualizado.getNombre());
        assertEquals(99, actualizado.getStock());
    }
}