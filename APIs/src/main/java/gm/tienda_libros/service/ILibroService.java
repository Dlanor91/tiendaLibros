package gm.tienda_libros.service;

import gm.tienda_libros.model.Libro;

import java.util.List;

public interface ILibroService {
    public List<Libro> listarLibros();

    public Libro buscarLibroPorId(Integer idLibro);

    public void upsertLibro(Libro libro);

    public boolean eliminarLibro(Integer idLibro);
}
