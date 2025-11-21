package gm.tienda.libros.service.imp;

import gm.tienda.libros.model.Cliente;
import gm.tienda.libros.repository.ClienteRepository;
import gm.tienda.libros.service.IClienteService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ClienteService implements IClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente obtenerClientePorId(Integer id) {

        Objects.requireNonNull(id, "El id no puede ser null");

        if (id <= 0) {
            throw new IllegalArgumentException("El ID de cliente debe ser mayor que cero.");
        }

        return clienteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + id));
    }

    @Override
    public Cliente registrarCliente(Cliente cliente) {
        Objects.requireNonNull(cliente, "El cliente no puede ser null");

        Optional<Cliente> existeClienteConEmail = clienteRepository.findByEmail(cliente.getEmail());
        if(existeClienteConEmail.isPresent()){
            throw new EntityExistsException("Ya existe un cliente con el email: " + cliente.getEmail());
        }

        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente actualizarCliente(Integer id, Cliente cliente) {
        Objects.requireNonNull(cliente, "El cliente no puede ser null");


        Cliente clienteExistente  = obtenerClientePorId(id);

        Objects.requireNonNull(cliente.getEmail(), "El email no puede ser null");

        // Validacion de email duplicado
        clienteRepository.findByEmail(cliente.getEmail())
                .filter(c -> !c.getId().equals(id))  // Si el encontrado NO es el mismo cliente
                .ifPresent(c -> {
                    throw new EntityExistsException(
                            "Ya existe un cliente con el email: " + cliente.getEmail()
                    );
                });

        //Modifico el resto de campos
        clienteExistente.setNombre(cliente.getNombre());
        clienteExistente.setTelefono(cliente.getTelefono());
        clienteExistente.setEmail(cliente.getEmail());

        return clienteRepository.save(clienteExistente);
    }

    @Override
    public void eliminarCliente(Integer id) {

        Objects.requireNonNull(id, "El id no puede ser null");

        if(id <=0)
        {
            throw new IllegalArgumentException("El ID de cliente debe ser mayor que cero.");
        }

        //Verifico si existe el cliente
        Cliente cliente = obtenerClientePorId(id);

        clienteRepository.delete(cliente);
    }
}
