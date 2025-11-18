package gm.tienda.libros.repository;

import gm.tienda.libros.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
    Optional<Venta> findByCodigo(String codigo);
}
