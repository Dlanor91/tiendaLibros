package gm.tienda.libros.event;

public record StockRespuestaEvent(
    Integer ventaId,
    String estado,
    String mensaje
) {}
