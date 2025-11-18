package gm.tienda.libros.ValidationTest;

import gm.tienda.libros.model.Autor;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AutorValidationTest {

    private Validator validator;

    @BeforeAll
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Autor crearAutorValido() {
        Autor autor = new Autor();
        autor.setNombre("Gabriel");
        autor.setApellidos("García Márquez");
        autor.setLibros(List.of()); // lista vacía válida
        return autor;
    }

    @Test
    @DisplayName("Debe fallar validación cuando nombre es null")
    void debeFallarCuandoNombreEsNull() {
        Autor autor = crearAutorValido();
        autor.setNombre(null);

        Set<ConstraintViolation<Autor>> violaciones = validator.validate(autor);

        assertThat(violaciones)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre"));
    }

    @Test
    @DisplayName("Debe fallar validación cuando apellidos es null")
    void debeFallarCuandoApellidosEsNull() {
        Autor autor = crearAutorValido();
        autor.setApellidos(null);

        Set<ConstraintViolation<Autor>> violaciones = validator.validate(autor);

        assertThat(violaciones)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("apellidos"));
    }

    @Test
    @DisplayName("Debe pasar validación cuando el objeto es completamente válido")
    void objetoValidoNoTieneViolations() {
        Autor autor = crearAutorValido();

        Set<ConstraintViolation<Autor>> violaciones = validator.validate(autor);

        assertThat(violaciones).isEmpty();
    }
}
