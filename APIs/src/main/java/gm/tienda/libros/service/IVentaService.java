package gm.tienda.libros.service;

import gm.tienda.libros.dto.VentaDTO;
import gm.tienda.libros.model.Venta;

import java.util.List;

public interface IVentaService {
    public List<VentaDTO> listarVentas();

    public Venta obtenerVentaByCodigo(String codigo);

    public Venta crearVenta(Venta venta);

    public Venta actualizarVenta(String codigo, Venta venta);

    public void eliminarVenta(String codigo);
}
