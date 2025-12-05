package gm.tienda.libros.service.imp;

import gm.tienda.libros.dto.DetallesVentaDTO;
import gm.tienda.libros.model.DetallesVenta;
import gm.tienda.libros.repository.DetallesVentaRepository;
import gm.tienda.libros.service.IDetallesVentaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetallesVentaService implements IDetallesVentaService {

    private final DetallesVentaRepository detallesVentaRepository;

    public DetallesVentaService(DetallesVentaRepository detallesVentaRepository){
        this.detallesVentaRepository = detallesVentaRepository;
    }

    @Override
    public List<DetallesVentaDTO > listar() {
        return detallesVentaRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public DetallesVentaDTO  buscarPorId(Integer id) {
        DetallesVenta dv = detallesVentaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Detalle de venta no encontrado"));

        return mapToDto(dv);
    }

    @Override
    public DetallesVenta crear(DetallesVenta detallesVenta) {
        return detallesVentaRepository.save(detallesVenta);
    }

    @Override
    public void eliminar(Integer id) {
        if (!detallesVentaRepository.existsById(id)) {
            throw new EntityNotFoundException("Detalle de venta no existe");
        }
        detallesVentaRepository.deleteById(id);
    }

    private DetallesVentaDTO mapToDto(DetallesVenta dv) {
        return new DetallesVentaDTO(
                dv.getId(),
                dv.getCantidad(),
                dv.getPrecioUnitario(),
                dv.getCodMoneda(),
                dv.getSubtotal(),
                dv.getIsbnLibro(),
                dv.getLibro().getNombre(),
                dv.getLibro().getGeneroLiterario().getNombre(),
                dv.getCodVenta(),
                dv.getVenta().getFecha()
        );
    }
}
