package gm.tienda.libros.ServiceTest;

import gm.tienda.libros.model.Cliente;
import gm.tienda.libros.repository.ClienteRepository;
import gm.tienda.libros.service.imp.ClienteService;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    // ---------- CREATE ----------
    @Test
    @DisplayName("Debe crear un cliente cuando el email no existe")
    void debeCrearClienteSiEmailNoExiste() {
        when(clienteRepository.findByEmail("nuevo@test.com")).thenReturn(Optional.empty());

        Cliente nuevo = new Cliente();
        nuevo.setNombre("Ana");
        nuevo.setEmail("nuevo@test.com");
        nuevo.setTelefono("123");

        clienteService.registrarCliente(nuevo);

        verify(clienteRepository).save(nuevo);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el email ya existe")
    void debeLanzarExcepcionSiEmailYaExiste() {
        Cliente existente = new Cliente();
        existente.setEmail("duplicado@test.com");
        when(clienteRepository.findByEmail("duplicado@test.com")).thenReturn(Optional.of(existente));

        Cliente nuevo = new Cliente();
        nuevo.setEmail("duplicado@test.com");

        assertThatThrownBy(() -> clienteService.registrarCliente(nuevo))
                .isInstanceOf(EntityExistsException.class)
                .hasMessageContaining("Ya existe");
    }

    // ---------- READ ----------
    @Test
    @DisplayName("Debe obtener un cliente por su ID")
    void debeObtenerClientePorId() {
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombre("Juan");
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        Cliente resultado = clienteService.obtenerClientePorId(1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el cliente no existe al buscar por ID")
    void debeLanzarExcepcionSiClienteNoExiste() {
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.obtenerClientePorId(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no encontrado");
    }

    // ---------- UPDATE ----------
    @Test
    @DisplayName("Debe actualizar un cliente existente correctamente")
    void debeActualizarClienteExistente() {
        Cliente existente = new Cliente();
        existente.setId(1);
        existente.setNombre("Pedro");
        existente.setEmail("pedro@test.com");
        existente.setTelefono("111");

        when(clienteRepository.findById(1)).thenReturn(Optional.of(existente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(existente);

        Cliente cambios = new Cliente();
        cambios.setNombre("Pedro Actualizado");
        cambios.setEmail("pedro@test.com");
        cambios.setTelefono("999");

        Cliente actualizado = clienteService.actualizarCliente(1, cambios);

        assertThat(actualizado.getNombre()).isEqualTo("Pedro Actualizado");
        assertThat(actualizado.getTelefono()).isEqualTo("999");
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar actualizar un cliente inexistente")
    void debeLanzarExcepcionAlActualizarClienteInexistente() {
        when(clienteRepository.findById(10)).thenReturn(Optional.empty());

        Cliente cambios = new Cliente();
        cambios.setNombre("Nuevo");

        assertThatThrownBy(() -> clienteService.actualizarCliente(10, cambios))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no encontrado");
    }

    // ---------- DELETE ----------
    @Test
    @DisplayName("Debe eliminar un cliente existente correctamente")
    void debeEliminarClienteExistente() {
        Cliente existente = new Cliente();
        existente.setId(1);

        when(clienteRepository.findById(1)).thenReturn(Optional.of(existente));

        clienteService.eliminarCliente(1);

        verify(clienteRepository).delete(existente);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar eliminar un cliente inexistente")
    void debeLanzarExcepcionAlEliminarClienteInexistente() {
        when(clienteRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.eliminarCliente(5))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no encontrado");
    }

    // ---------- LIST ----------
    @Test
    @DisplayName("Debe listar todos los clientes existentes")
    void debeListarTodosLosClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of(new Cliente(), new Cliente()));

        List<Cliente> clientes = clienteService.listarClientes();

        assertThat(clientes).hasSize(2);
        verify(clienteRepository).findAll();
    }
}

