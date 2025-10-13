package gm.tienda_libros.ValidationTest;

import gm.tienda_libros.model.Cliente;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ClienteValidacionTest {
    private final Validator validator;

    public ClienteValidacionTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void debeFallarCuandoNombreEsNull() {
        Cliente cliente = new Cliente();
        cliente.setNombre(null);
        cliente.setEmail("juan@test.com");
        cliente.setTelefono("099123456");

        Set<ConstraintViolation<Cliente>> violaciones = validator.validate(cliente);
        assertThat(violaciones).isNotEmpty();
    }

    @Test
    void debeFallarCuandoEmailNoEsUnicoONull() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan Pérez");
        cliente.setEmail(null);
        cliente.setTelefono("099123456");

        Set<ConstraintViolation<Cliente>> violaciones = validator.validate(cliente);
        assertThat(violaciones).isNotEmpty();
    }

    @Test
    void debeValidarClienteCorrecto() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Ana García");
        cliente.setEmail("ana@correo.com");
        cliente.setTelefono("091111111");

        Set<ConstraintViolation<Cliente>> violaciones = validator.validate(cliente);
        assertThat(violaciones).isEmpty();
    }
}
