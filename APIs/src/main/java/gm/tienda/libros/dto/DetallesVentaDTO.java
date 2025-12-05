package gm.tienda.libros.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DetallesVentaDTO(
        Integer id,
        Integer cantidad,
        BigDecimal precioUnitario,
        String codMoneda,
        BigDecimal subtotal,
        String isbnLibro,
        String nombreLibro,
        String tipoGeneroLiterario,
        String codVenta,
        LocalDateTime fechaVenta
) {}