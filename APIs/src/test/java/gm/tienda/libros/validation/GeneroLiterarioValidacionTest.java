package gm.tienda.libros.validation;

import gm.tienda.libros.model.GeneroLiterario;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GeneroLiterarioValidacionTest {

    private Validator validator;

    @BeforeAll
    void setupValidatorFactory() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debe fallar validación cuando nombre es null")
    void debeFallarCuandoNombreEsNull() {
        GeneroLiterario g = new GeneroLiterario();
        g.setNombre(null);
        g.setCodigo("ABC");

        Set<ConstraintViolation<GeneroLiterario>> violations = validator.validate(g);

        assertThat(violations).satisfies(list -> {
            assertThat(list).hasSizeGreaterThanOrEqualTo(1);
            assertThat(list).anyMatch(v ->
                    v.getPropertyPath().toString().equals("nombre")
                            && v.getMessage().contains("no puede estar en blanco"));
        });
    }

    @Test
    @DisplayName("Debe fallar validación cuando codigo es null")
    void debeFallarCuandoCodigoEsNull() {
        GeneroLiterario g = new GeneroLiterario();
        g.setNombre("Ficción");
        g.setCodigo(null);

        Set<ConstraintViolation<GeneroLiterario>> violations = validator.validate(g);

        assertThat(violations).satisfies(list -> {
            assertThat(list).hasSizeGreaterThanOrEqualTo(1);
            assertThat(list).anyMatch(v ->
                    v.getPropertyPath().toString().equals("codigo") &&
                            v.getMessage().contains("no puede estar en blanco"));
        });
    }

    @Test
    @DisplayName("Debe fallar validación cuando nombre excede el max length")
    void debeFallarCuandoNombreExcedeLongitud() {
        StringBuilder largo = new StringBuilder();
        for (int i = 0; i < 60; i++) largo.append("a"); // 60 > 50
        GeneroLiterario g = new GeneroLiterario();
        g.setNombre(largo.toString());
        g.setCodigo("X01");

        Set<ConstraintViolation<GeneroLiterario>> violations = validator.validate(g);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("nombre"));
    }

    @Test
    @DisplayName("Objeto válido: no debe producir violations")
    void objetoValidoNoTieneViolations() {
        GeneroLiterario g = new GeneroLiterario();
        g.setNombre("Ciencia Ficción");
        g.setCodigo("SF1");

        Set<ConstraintViolation<GeneroLiterario>> violations = validator.validate(g);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Debe fallar validación cuando codigo excede longitud")
    void debeFallarCuandoCodigoExcedeLongitud() {
        GeneroLiterario g = new GeneroLiterario();
        g.setNombre("Ficción");
        g.setCodigo("LONG"); // 4 > 3

        Set<ConstraintViolation<GeneroLiterario>> violations = validator.validate(g);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("codigo")
                        && v.getMessage().contains("máximo"));
    }

    @Test
    @DisplayName("Debe fallar validación cuando nombre está vacío")
    void debeFallarCuandoNombreVacio() {
        GeneroLiterario g = new GeneroLiterario();
        g.setNombre("");
        g.setCodigo("F01");

        Set<ConstraintViolation<GeneroLiterario>> violations = validator.validate(g);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre"));
    }

    @Test
    @DisplayName("Debe fallar validación cuando codigo está vacío")
    void debeFallarCuandoCodigoVacio() {
        GeneroLiterario g = new GeneroLiterario();
        g.setNombre("Historia");
        g.setCodigo("");

        Set<ConstraintViolation<GeneroLiterario>> violations = validator.validate(g);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("codigo"));
    }
}
