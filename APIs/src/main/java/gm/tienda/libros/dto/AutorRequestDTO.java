package gm.tienda.libros.dto;

import jakarta.validation.constraints.NotBlank;

public record AutorRequestDTO(
        Integer id,
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,
        @NotBlank(message = "Los apellidos no pueden estar vacío")
        String apellidos
) {}
