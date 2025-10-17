package gm.tienda_libros.service.imp;

import gm.tienda_libros.DTOs.VentaDTO;
import gm.tienda_libros.model.Venta;
import gm.tienda_libros.repository.VentaRepository;
import gm.tienda_libros.service.IVentaService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VentaService implements IVentaService {

    @Autowired
    private VentaRepository ventaRepository;

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
    public Venta obtenerVentaById(Integer id) {
        if(id == null || id < 0){
            throw new IllegalArgumentException("El id no puede ser null o menor que 0");
        }

        return ventaRepository.findById(id).orElseThrow(()-> {
                    return new EntityNotFoundException("Venta no encontrada con ID: " + id);
                }
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
    public Venta actualizarVenta(Integer id, Venta venta) {
        return null;
    }

    @Override
    public void eliminarVenta(Integer id) {
        if(id == null || id < 0){
            throw new IllegalArgumentException("El id no puede ser null o menor que 0");
        }

        Venta venta = obtenerVentaById(id);

        ventaRepository.delete(venta);
    }
}
