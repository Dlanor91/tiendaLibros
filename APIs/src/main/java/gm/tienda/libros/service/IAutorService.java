package gm.tienda.libros.service;

import gm.tienda.libros.dto.AutorDTO;
import gm.tienda.libros.dto.AutorRequestDTO;
import gm.tienda.libros.model.Autor;

import java.util.List;

public interface IAutorService {
    List<AutorDTO> listarAutores();

    Autor obtenerAutorPorId(Integer id);

    Autor crearAutor(AutorRequestDTO autor);

    Autor actualizarAutor(Integer id, AutorRequestDTO autor);

    void eliminarAutor(Integer id);

    List<Autor> buscarAutoresNombre(String nombre);
}
