package gm.tienda.libros.dto;

import jakarta.validation.constraints.NotBlank;

public record AutorRequestDTO(
        Integer id,
        @NotBlank String nombre,
        @NotBlank String apellidos
) {}
