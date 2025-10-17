package gm.tienda_libros.ValidationTest;

import gm.tienda_libros.model.Venta;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VentaValidacionTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void debeDetectarCamposObligatoriosVacios() {
        Venta venta = new Venta();
        Set<ConstraintViolation<Venta>> violaciones = validator.validate(venta);

        assertThat(violaciones).hasSizeGreaterThan(0);
        assertThat(violaciones.stream().anyMatch(v -> v.getMessage().contains("no puede estar vacío"))).isTrue();
    }

    @Test
    void debeValidarTotalPositivoYClienteMayorQueCero() {
        Venta venta = new Venta(
                "V001",
                LocalDateTime.now(),
                new BigDecimal("-5.00"),
                "USD",
                0,
                null
        );

        Set<ConstraintViolation<Venta>> violaciones = validator.validate(venta);

        assertThat(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("mayor que 0"))).isTrue();
    }

    @Test
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
