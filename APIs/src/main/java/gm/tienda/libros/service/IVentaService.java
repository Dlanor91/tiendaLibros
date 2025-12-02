package gm.tienda.libros.service;

import gm.tienda.libros.dto.VentaDTO;
import gm.tienda.libros.model.Venta;

import java.util.List;

public interface IVentaService {
    List<VentaDTO> listarVentas();

    Venta obtenerVentaByCodigo(String codigo);

    Venta crearVenta(Venta venta);

    Venta actualizarVenta(String codigo, Venta venta);

    void eliminarVenta(String codigo);
}
