package gm.tienda.libros.service;

import gm.tienda.libros.dto.LibroDTO;
import gm.tienda.libros.dto.LibroRequestDTO;

import java.util.List;

public interface ILibroService {
    List<LibroDTO> listarLibros();

    LibroDTO buscarLibroPorIsbn(String isbn);

    LibroDTO  insertarLibro(LibroRequestDTO libro);

    LibroDTO  actualizarLibro(String isbn, LibroRequestDTO libro);

    void eliminarLibro(String isbn);

    List<LibroDTO> buscarLibrosCodGeneroLiterario(String codGenero);
}
