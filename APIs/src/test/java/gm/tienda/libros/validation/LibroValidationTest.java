package gm.tienda.libros.validation;

import gm.tienda.libros.model.Autor;
import gm.tienda.libros.model.GeneroLiterario;
import gm.tienda.libros.model.Libro;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LibroValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ---------- Helper ----------
    private Libro crearLibroValido() {
        Libro libro = new Libro();
        libro.setId(1);
        libro.setIsbn("ABC123456");
        libro.setNombre("Libro de Prueba");
        libro.setCodMoneda("USD");
        libro.setPrecio(new BigDecimal("19.99"));
        libro.setStock(10);
        libro.setDescripcion("Descripción válida del libro");
        libro.setFechaPublicacion(LocalDate.now().minusDays(1));
        libro.setCodGeneroLiterario("FIC");

        return libro;
    }

    // ---------- TESTS ----------

    @Test
    @DisplayName("Un libro válido no debe generar ninguna violación de validación")
    void libroValido_NoDebeGenerarViolaciones() {
        Libro libro = crearLibroValido();
        Set<ConstraintViolation<Libro>> violaciones = validator.validate(libro);
        assertTrue(violaciones.isEmpty(), "El libro válido no debe generar errores");
    }

    @Test
    @DisplayName("El ISBN no puede ser nulo")
    void isbn_NoPuedeSerNulo() {
        Libro libro = crearLibroValido();
        libro.setIsbn(null);

        Set<ConstraintViolation<Libro>> violaciones = validator.validate(libro);
        assertFalse(violaciones.isEmpty());
    }

    @Test
    @DisplayName("El nombre no puede ser nulo")
    void nombre_NoPuedeSerNulo() {
        Libro libro = crearLibroValido();
        libro.setNombre(null);

        Set<ConstraintViolation<Libro>> violaciones = validator.validate(libro);
        assertFalse(violaciones.isEmpty());
    }

    @Test
    @DisplayName("El stock no puede ser negativo — debe cumplir @Min(0)")
    void stock_NoPuedeSerNegativo() {
        Libro libro = crearLibroValido();
        libro.setStock(-1);

        Set<ConstraintViolation<Libro>> violaciones = validator.validate(libro);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("must be greater")));
    }

    @Test
    @DisplayName("El precio no puede ser nulo")
    void precio_NoPuedeSerNulo() {
        Libro libro = crearLibroValido();
        libro.setPrecio(null);

        Set<ConstraintViolation<Libro>> violaciones = validator.validate(libro);

        assertFalse(violaciones.isEmpty());
    }

    @Test
    @DisplayName("El código de género literario no puede ser nulo")
    void codGeneroLiterario_NoPuedeSerNulo() {
        Libro libro = crearLibroValido();
        libro.setCodGeneroLiterario(null);

        Set<ConstraintViolation<Libro>> violaciones = validator.validate(libro);

        assertFalse(violaciones.isEmpty());
    }

    @Test
    @DisplayName("La fecha de publicación no puede ser nula")
    void fechaPublicacion_NoPuedeSerNula() {
        Libro libro = crearLibroValido();
        libro.setFechaPublicacion(null);

        Set<ConstraintViolation<Libro>> violaciones = validator.validate(libro);

        assertFalse(violaciones.isEmpty());
    }

    @Test
    @DisplayName("La lista de autores puede estar vacía y sigue siendo válida")
    void autores_PuedeEstarVacio_PeroNoDebeRomper() {
        Libro libro = crearLibroValido();
        libro.setAutores(List.of());

        Set<ConstraintViolation<Libro>> violaciones = validator.validate(libro);

        assertTrue(violaciones.isEmpty(), "La lista vacía de autores es válida");
    }

    @Test
    @DisplayName("La relación con GéneroLiterario debe asignarse correctamente")
    void relacionGeneroLiterario_AsignacionCorrecta() {
        Libro libro = crearLibroValido();

        GeneroLiterario genero = new GeneroLiterario();
        genero.setCodigo("FIC");
        genero.setNombre("Ficción");

        libro.setGeneroLiterario(genero);

        assertNotNull(libro.getGeneroLiterario());
        assertEquals("FIC", libro.getGeneroLiterario().getCodigo());
    }

    @Test
    @DisplayName("Se pueden asignar múltiples autores correctamente en la relación ManyToMany")
    void relacionAutores_PuedeAsignarMultipleAutores() {
        Libro libro = crearLibroValido();

        Autor a1 = new Autor();
        a1.setId(1);
        a1.setNombre("Autor 1");

        Autor a2 = new Autor();
        a2.setId(2);
        a2.setNombre("Autor 2");

        libro.setAutores(List.of(a1, a2));

        assertEquals(2, libro.getAutores().size());
        assertEquals("Autor 1", libro.getAutores().get(0).getNombre());
    }
}
