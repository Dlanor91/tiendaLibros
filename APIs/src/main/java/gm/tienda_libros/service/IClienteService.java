package gm.tienda_libros.service;

import gm.tienda_libros.model.Cliente;

import java.util.List;

public interface IClienteService {
    public List<Cliente> listarClientes();

    public Cliente obtenerClientePorId(Integer id);

    public Cliente registrarCliente(Cliente cliente);

    public Cliente actualizarCliente(Integer id, Cliente cliente);

    public void eliminarCliente(Integer id);
}
