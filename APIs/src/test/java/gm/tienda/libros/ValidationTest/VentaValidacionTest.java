package gm.tienda.libros.ValidationTest;

import gm.tienda.libros.model.Venta;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VentaValidacionTest {

    private Validator validator;

    @BeforeAll
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debe detectar todos los campos obligatorios vacíos o null")
    void debeDetectarCamposObligatoriosVacios() {
        Venta venta = new Venta(); // todos los campos nulos por defecto

        Set<ConstraintViolation<Venta>> violaciones = validator.validate(venta);

        assertThat(violaciones).isNotEmpty();

        // obtener lista de mensajes por propiedad para mayor claridad
        Set<String> camposConErrores = violaciones.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toSet());

        assertThat(camposConErrores).anyMatch(m -> m.contains("codigo"));
        assertThat(camposConErrores).anyMatch(m -> m.contains("fecha"));
        assertThat(camposConErrores).anyMatch(m -> m.contains("total"));
        assertThat(camposConErrores).anyMatch(m -> m.contains("codMoneda"));
        assertThat(camposConErrores).anyMatch(m -> m.contains("idCliente"));
    }

    @Test
    @DisplayName("Debe fallar validación cuando total es negativo o idCliente <= 0")
    void debeValidarTotalPositivoYClienteMayorQueCero() {
        Venta venta = new Venta(
                "V001",
                LocalDateTime.now(),
                new BigDecimal("-5.00"), // total negativo
                "USD",
                0,                       // idCliente inválido
                null
        );

        Set<ConstraintViolation<Venta>> violaciones = validator.validate(venta);

        // verificar que haya violación en total
        assertThat(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("total") &&
                        v.getMessage().contains("mayor que 0")))
                .isTrue();

        // verificar que haya violación en idCliente
        assertThat(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("idCliente") &&
                        v.getMessage().contains("mayor que 0")))
                .isTrue();
    }

    @Test
    @DisplayName("Venta válida no debe generar violations")
    void ventaValidaNoDebeGenerarViolaciones() {
        Venta venta = new Venta(
                "V001",
                LocalDateTime.now(),
                new BigDecimal("100.00"),
                "USD",
                1,
                null
        );

        Set<ConstraintViolation<Venta>> violaciones = validator.validate(venta);
        assertThat(violaciones).isEmpty();
    }
}
