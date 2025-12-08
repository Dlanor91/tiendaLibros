package gm.tienda.libros.dto;

public record VentaMLDTO(
        Integer id,
        String isbnLibro,
        Integer cantidad,
        Boolean procesada
) {
}
