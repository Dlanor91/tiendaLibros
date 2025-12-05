package gm.tienda.libros.validation;

import gm.tienda.libros.model.DetallesVenta;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DetallesVentaValidationTest {

    private Validator validator;

    @BeforeAll
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private DetallesVenta crearDetalleValido() {
        DetallesVenta d = new DetallesVenta();
        d.setCantidad(1);
        d.setPrecioUnitario(new BigDecimal("10.00"));
        d.setCodMoneda("USD");
        d.setSubtotal(new BigDecimal("10.00"));
        d.setIsbnLibro("TEST123");
        d.setCodVenta("V001");
        return d;
    }

    // ---------- CANTIDAD ----------
    @Test
    @DisplayName("Debe fallar validación cuando cantidad es negativa (violación de @Min)")
    void debeFallarCuandoCantidadEsNegativa() {
        DetallesVenta d = crearDetalleValido();
        d.setCantidad(-1);

        Set<ConstraintViolation<DetallesVenta>> violaciones = validator.validate(d);

        assertThat(violaciones)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cantidad"));
    }

    @Test
    @DisplayName("Debe fallar validación cuando cantidad es null")
    void debeFallarCuandoCantidadEsNull() {
        DetallesVenta d = crearDetalleValido();
        d.setCantidad(null);

        Set<ConstraintViolation<DetallesVenta>> violaciones = validator.validate(d);

        assertThat(violaciones)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cantidad"));
    }

    // ---------- PRECIO UNITARIO ----------
    @Test
    @DisplayName("Debe fallar validación cuando precioUnitario es null")
    void debeFallarCuandoPrecioUnitarioEsNull() {
        DetallesVenta d = crearDetalleValido();
        d.setPrecioUnitario(null);

        Set<ConstraintViolation<DetallesVenta>> violaciones = validator.validate(d);

        assertThat(violaciones)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("precioUnitario"));
    }

    // ---------- COD MONEDA ----------
    @Test
    @DisplayName("Debe fallar validación cuando codMoneda es null")
    void debeFallarCuandoCodMonedaEsNull() {
        DetallesVenta d = crearDetalleValido();
        d.setCodMoneda(null);

        Set<ConstraintViolation<DetallesVenta>> violaciones = validator.validate(d);

        assertThat(violaciones)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("codMoneda"));
    }

    @Test
    @DisplayName("Debe fallar validación cuando codMoneda tiene más de 3 caracteres")
    void debeFallarCuandoCodMonedaEsMuyLargo() {
        DetallesVenta d = crearDetalleValido();
        d.setCodMoneda("USDD"); // 4 caracteres

        Set<ConstraintViolation<DetallesVenta>> violaciones = validator.validate(d);

        assertThat(violaciones).isNotEmpty();
    }

    // ---------- SUBTOTAL ----------
    @Test
    @DisplayName("Debe fallar validación cuando subtotal es null")
    void debeFallarCuandoSubtotalEsNull() {
        DetallesVenta d = crearDetalleValido();
        d.setSubtotal(null);

        Set<ConstraintViolation<DetallesVenta>> violaciones = validator.validate(d);

        assertThat(violaciones)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("subtotal"));
    }

    // ---------- ISBN LIBRO ----------
    @Test
    @DisplayName("Debe fallar validación cuando isbnLibro es null")
    void debeFallarCuandoIsbnEsNull() {
        DetallesVenta d = crearDetalleValido();
        d.setIsbnLibro(null);

        Set<ConstraintViolation<DetallesVenta>> violaciones = validator.validate(d);

        assertThat(violaciones)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("isbnLibro"));
    }

    @Test
    @DisplayName("Debe fallar cuando isbnLibro supera los 20 caracteres")
    void debeFallarCuandoIsbnEsMuyLargo() {
        DetallesVenta d = crearDetalleValido();
        d.setIsbnLibro("1234567890123456789012345"); // 25 chars

        Set<ConstraintViolation<DetallesVenta>> violaciones = validator.validate(d);

        assertThat(violaciones).isNotEmpty();
    }

    // ---------- COD VENTA ----------
    @Test
    @DisplayName("Debe fallar validación cuando codVenta es null")
    void debeFallarCuandoCodVentaEsNull() {
        DetallesVenta d = crearDetalleValido();
        d.setCodVenta(null);

        Set<ConstraintViolation<DetallesVenta>> violaciones = validator.validate(d);

        assertThat(violaciones)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("codVenta"));
    }

    @Test
    @DisplayName("Debe fallar cuando codVenta supera los 10 caracteres")
    void debeFallarCuandoCodVentaEsMuyLargo() {
        DetallesVenta d = crearDetalleValido();
        d.setCodVenta("12345678901"); // 11 chars

        Set<ConstraintViolation<DetallesVenta>> violaciones = validator.validate(d);

        assertThat(violaciones).isNotEmpty();
    }

    // ---------- OBJETO VÁLIDO ----------
    @Test
    @DisplayName("Debe pasar validación cuando el objeto es completamente válido")
    void objetoValidoNoTieneViolaciones() {
        DetallesVenta d = crearDetalleValido();

        Set<ConstraintViolation<DetallesVenta>> violaciones = validator.validate(d);

        assertThat(violaciones).isEmpty();
    }

}
