package gm.tienda.libros.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record LibroRequestDTO(
        Integer id,
        @NotBlank(message = "El isbn no puede estar vacío")
        String isbn,
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,
        @NotBlank(message = "El codMoneda no puede estar vacío")
        String codMoneda,
        @NotNull(message = "El precio no puede estar vacío")
        @Positive(message = "El precio debe seer mayor que 0")
        @Min(0)
        BigDecimal precio,
        @NotNull(message = "El stock no puede estar vacío")
        @Positive(message = "El stock debe seer mayor que 0")
        @Min(0)
        Integer stock,
        @NotBlank(message = "El codigo de descripcion no puede estar vacío")
        String descripcion,
        @NotNull(message = "La fechaPublicacion no puede estar vacío")
        LocalDate fechaPublicacion,
        @NotBlank(message = "El codigo de genero literario no puede estar vacío")
        String codGeneroLiterario,
        @NotNull(message = "Debe incluir al menos un autor")
        List<Integer> autoresIds
) {}
