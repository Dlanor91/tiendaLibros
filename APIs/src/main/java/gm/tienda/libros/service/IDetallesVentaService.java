package gm.tienda.libros.service;

import gm.tienda.libros.dto.DetallesVentaDTO;
import gm.tienda.libros.model.DetallesVenta;

import java.util.List;

public interface IDetallesVentaService {
    List<DetallesVentaDTO> listar();

    DetallesVentaDTO buscarPorId(Integer id);

    DetallesVenta crear(DetallesVenta detallesVenta);

    void eliminar(Integer id);
}
