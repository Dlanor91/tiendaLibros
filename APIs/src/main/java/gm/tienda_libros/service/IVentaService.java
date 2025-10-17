package gm.tienda_libros.service;

import gm.tienda_libros.DTOs.VentaDTO;
import gm.tienda_libros.model.Venta;

import java.util.List;

public interface IVentaService {
    public List<VentaDTO> listarVentas();

    public Venta obtenerVentaById(Integer id);

    public Venta crearVenta(Venta venta);

    public Venta actualizarVenta(Integer id, Venta venta);

    public void eliminarVenta(Integer id);
}
