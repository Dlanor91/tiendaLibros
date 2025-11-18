package gm.tienda.libros.service.imp;

import gm.tienda.libros.dto.VentaDTO;
import gm.tienda.libros.model.Venta;
import gm.tienda.libros.repository.VentaRepository;
import gm.tienda.libros.service.IVentaService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VentaService implements IVentaService {

    private final VentaRepository ventaRepository;

    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    @Override
    public List<VentaDTO> listarVentas() {
        return ventaRepository.findAll(Sort.by("codigo"))
                .stream()
                .map(v -> new VentaDTO(
                        v.getId(),
                        v.getCodigo(),
                        v.getFecha(),
                        v.getTotal(),
                        v.getCodMoneda(),
                        v.getIdCliente(),
                        v.getCliente() != null ? v.getCliente().getNombre() : null
                ))
                .toList();
    }

    @Override
    public Venta obtenerVentaByCodigo(String codigo) {
        if(codigo == null || codigo.trim().isBlank()){
            throw new IllegalArgumentException("El codigo de venta debe ser distinto de null y de vacio");
        }

        return ventaRepository.findByCodigo(codigo).orElseThrow(()->
              new EntityNotFoundException("Venta no encontrada con Codigo: " + codigo)
        );
    }

    @Override
    public Venta crearVenta(Venta venta) {
        if(venta == null){
            throw new IllegalArgumentException("La venta no puede ser null");
        }

        if(venta.getIdCliente() == null || venta.getIdCliente() == 0){
            throw new IllegalArgumentException("El id de cliente no puede ser null o menor que 1");
        }

        Optional<Venta> ventaEncontrada = ventaRepository.findByCodigo(venta.getCodigo());
        if(ventaEncontrada.isPresent()){
            throw new EntityExistsException("Ya existe una venta registrada con ese código");
        }

        return ventaRepository.save(venta);
    }

    @Override
    public Venta actualizarVenta(String codigo, Venta venta) {
        if(venta== null ){
            throw new IllegalArgumentException("la venta no puede ser null");
        }

        if(codigo== null || codigo.trim().isBlank()){
            throw new IllegalArgumentException("El código no puede ser null ni estar vacío");
        }

        if (!venta.getCodigo().equals(codigo)) {
            throw new IllegalArgumentException("El código de la venta no puede cambiarse");
        }

        Venta ventaExistente = obtenerVentaByCodigo(codigo);

        ventaExistente.setFecha(venta.getFecha());
        ventaExistente.setTotal(venta.getTotal());
        ventaExistente.setCodMoneda(venta.getCodMoneda());
        ventaExistente.setIdCliente(venta.getIdCliente());

        return ventaRepository.save(ventaExistente);
    }

    @Override
    public void eliminarVenta(String codigo) {
        if(codigo== null || codigo.trim().isBlank()){
            throw new IllegalArgumentException("El código no puede ser null ni estar vacío");
        }

        Venta venta = obtenerVentaByCodigo(codigo);

        ventaRepository.delete(venta);
    }
}
