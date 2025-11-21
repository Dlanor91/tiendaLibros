package gm.tienda.libros.validation;

import gm.tienda.libros.model.Cliente;
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
class ClienteValidacionTest {

    private Validator validator;

    @BeforeAll
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debe fallar validación cuando nombre es null")
    void debeFallarCuandoNombreEsNull() {
        Cliente cliente = new Cliente();
        cliente.setNombre(null);
        cliente.setEmail("juan@test.com");
        cliente.setTelefono("099123456");

        Set<ConstraintViolation<Cliente>> violaciones = validator.validate(cliente);

        assertThat(violaciones)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")
                        && v.getMessage().contains("no puede estar vacío"));
    }

    @Test
    @DisplayName("Debe fallar validación cuando email es null")
    void debeFallarCuandoEmailEsNull() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan Pérez");
        cliente.setEmail(null);
        cliente.setTelefono("099123456");

        Set<ConstraintViolation<Cliente>> violaciones = validator.validate(cliente);

        assertThat(violaciones)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")
                        && v.getMessage().contains("no puede estar vacío"));
    }

    @Test
    @DisplayName("Debe fallar validación cuando email no tiene formato válido")
    void debeFallarCuandoEmailFormatoIncorrecto() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan Pérez");
        cliente.setEmail("email-invalido");
        cliente.setTelefono("099123456");

        Set<ConstraintViolation<Cliente>> violaciones = validator.validate(cliente);

        assertThat(violaciones)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")
                        && v.getMessage().contains("debe tener un formato válido"));
    }

    @Test
    @DisplayName("Debe fallar validación cuando telefono es null")
    void debeFallarCuandoTelefonoEsNull() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan Pérez");
        cliente.setEmail("juan@test.com");
        cliente.setTelefono(null);

        Set<ConstraintViolation<Cliente>> violaciones = validator.validate(cliente);

        assertThat(violaciones)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("telefono")
                        && v.getMessage().contains("no puede estar vacío"));
    }

    @Test
    @DisplayName("Debe fallar validación cuando nombre excede el maximo de caracteres")
    void debeFallarCuandoNombreExcedeMax() {
        Cliente cliente = new Cliente();
        cliente.setNombre("A".repeat(60)); // 60 > 50
        cliente.setEmail("juan@test.com");
        cliente.setTelefono("099123456");

        Set<ConstraintViolation<Cliente>> violaciones = validator.validate(cliente);

        assertThat(violaciones)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")
                        && v.getMessage().contains("maximo de 50 caracteres"));
    }

    @Test
    @DisplayName("Objeto válido: no debe producir violations")
    void objetoValidoNoTieneViolations() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Ana García");
        cliente.setEmail("ana@correo.com");
        cliente.setTelefono("091111111");

        Set<ConstraintViolation<Cliente>> violaciones = validator.validate(cliente);

        assertThat(violaciones).isEmpty();
    }
}
