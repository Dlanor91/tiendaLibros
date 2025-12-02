package gm.tienda.libros.service;

import gm.tienda.libros.model.Cliente;

import java.util.List;

public interface IClienteService {
    List<Cliente> listarClientes();

    Cliente obtenerClientePorId(Integer id);

    Cliente registrarCliente(Cliente cliente);

    Cliente actualizarCliente(Integer id, Cliente cliente);

    void eliminarCliente(Integer id);
}
