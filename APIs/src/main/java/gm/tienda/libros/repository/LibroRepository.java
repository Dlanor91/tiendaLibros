package gm.tienda.libros.repository;

import gm.tienda.libros.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Integer> {
    Libro findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    List<Libro> findByCodGeneroLiterario(String codGenero);
}