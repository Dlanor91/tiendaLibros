package gm.tienda_libros.service.imp;

import gm.tienda_libros.model.Cliente;
import gm.tienda_libros.repository.ClienteRepository;
import gm.tienda_libros.service.IClienteService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService implements IClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente obtenerClientePorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID de cliente debe ser mayor que cero.");
        }

        return clienteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + id));
    }

    @Override
    public Cliente registrarCliente(Cliente cliente) {
        if(cliente == null){
            throw new IllegalArgumentException("El cliente no puede ser null.");
        }

        Optional<Cliente> existeClienteConEmail = clienteRepository.findByEmail(cliente.getEmail());
        if(existeClienteConEmail.isPresent()){
            throw new EntityExistsException("Ya existe un cliente con el email: " + cliente.getEmail());
        }

        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente actualizarCliente(Integer id, Cliente cliente) {
        if(cliente == null){
            throw new IllegalArgumentException("El cliente no puede ser null");
        }

        Cliente clienteExistente  = obtenerClientePorId(id);

        //No duplicar un email existente
        if(!clienteExistente.getEmail().equals(clienteExistente.getEmail())){
            boolean emailDuplicado = clienteRepository.findByEmail(clienteExistente.getEmail()).isPresent();
            if(emailDuplicado){
                throw new EntityExistsException("Ya existe un cliente con el email: " + cliente.getEmail());
            }
        }

        //Modifico el resto de campos
        clienteExistente.setNombre(cliente.getNombre());
        clienteExistente.setTelefono(cliente.getTelefono());
        clienteExistente.setEmail(cliente.getEmail());

        return clienteRepository.save(clienteExistente);
    }

    @Override
    public void eliminarCliente(Integer id) {
        if(id == null || id <=0)
        {
            throw new IllegalArgumentException("El ID de cliente debe ser mayor que cero.");
        }

        //Verifico si existe el cliente
        Cliente cliente = obtenerClientePorId(id);

        clienteRepository.delete(cliente);
    }
}
