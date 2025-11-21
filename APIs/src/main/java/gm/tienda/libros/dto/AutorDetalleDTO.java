package gm.tienda.libros.dto;

import java.util.List;

public record AutorDetalleDTO(
        Integer id,
        String nombre,
        String apellidos,
        List<LibroDTO> libros
) {}
