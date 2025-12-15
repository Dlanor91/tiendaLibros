package gm.tienda.libros.event;

public record VentaCreadaEvent(
        Integer idVenta,
        String isbnLibro,
        Integer cantidad
) {}
