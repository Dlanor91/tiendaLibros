package gm.tienda_libros.repository;

import gm.tienda_libros.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Integer> {
    List<Autor> findByNombreContainingIgnoreCaseOrderByNombre(String nombre);
}
