package gm.tienda.libros.service;

import gm.tienda.libros.model.Libro;

import java.util.List;

public interface ILibroService {
    public List<Libro> listarLibros();

    public Libro buscarLibroPorId(Integer idLibro);

    public void upsertLibro(Libro libro);

    public boolean eliminarLibro(Integer idLibro);
}
