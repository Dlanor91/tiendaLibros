package gm.tienda.libros.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LibroDTO(
        String isbn,
        String nombre,
        String codMoneda,
        BigDecimal precio,
        Integer stock,
        String descripcion,
        LocalDate fechaPublicacion,
        String codGeneroLiterario,
        String nombreGeneroLiterario
) {}
