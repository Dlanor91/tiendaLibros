package gm.tienda.libros.repository;

import gm.tienda.libros.model.GeneroLiterario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeneroLiterarioRepository extends JpaRepository<GeneroLiterario,Integer> {
    Optional<GeneroLiterario> findByCodigo(String codigo);
}
