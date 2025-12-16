package gm.tienda.libros.event;

public record VentaRecibidaEvent(
        Integer idVenta,
        String isbnLibro,
        Integer cantidad
) {}
