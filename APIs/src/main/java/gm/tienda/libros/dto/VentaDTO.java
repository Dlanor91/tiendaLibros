package gm.tienda.libros.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record  VentaDTO (
    Integer id,
    String codigo,
    LocalDateTime fecha,
    BigDecimal total,
    String codMoneda,
    Integer idCliente,
    String nombreCliente
) {}
