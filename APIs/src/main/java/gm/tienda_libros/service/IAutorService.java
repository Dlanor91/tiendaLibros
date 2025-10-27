package gm.tienda_libros.service;

import gm.tienda_libros.model.Autor;

import java.util.List;

public interface IAutorService {
    List<Autor> listarAutores();

    Autor obtenerAutorById(Integer id);

    Autor crearAutor(Autor autor);

    Autor actualizarAutor(Integer id, Autor autor);

    void eliminarAutor(Integer id);

    List<Autor> buscarAutoresNombre(String nombre);
}
