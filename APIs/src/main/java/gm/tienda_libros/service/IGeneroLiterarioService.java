package gm.tienda_libros.service;

import gm.tienda_libros.model.GeneroLiterario;

import java.util.List;

public interface IGeneroLiterarioService {
    List<GeneroLiterario> listarGenerosLiterarios();

    GeneroLiterario obtenerGeneroLiterarioByCodigo(String codigo);

    GeneroLiterario agregarGeneroLiterario(GeneroLiterario generoLiterario);

    GeneroLiterario actualizarGeneroLiterario(String codigo, GeneroLiterario generoLiterario);

    void eliminarGeneroLiterario(String codigo);
}
