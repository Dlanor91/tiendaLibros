package gm.tienda.libros.service;

import gm.tienda.libros.dto.AutorDTO;
import gm.tienda.libros.dto.AutorDetalleDTO;
import gm.tienda.libros.dto.AutorRequestDTO;
import gm.tienda.libros.model.Autor;

import java.util.List;

public interface IAutorService {
    List<AutorDTO> listarAutores();

    AutorDetalleDTO obtenerAutorPorId(Integer id);

    Autor crearAutor(AutorRequestDTO autor);

    Autor actualizarAutor(Integer id, AutorRequestDTO autor);

    void eliminarAutor(Integer id);

    List<Autor> buscarAutoresNombre(String nombre);
}
