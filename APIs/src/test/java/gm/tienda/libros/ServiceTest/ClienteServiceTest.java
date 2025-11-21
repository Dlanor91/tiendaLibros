package gm.tienda.libros.ServiceTest;

import gm.tienda.libros.model.Cliente;
import gm.tienda.libros.repository.ClienteRepository;
import gm.tienda.libros.service.imp.ClienteService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
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

    // ===============================================================
    //                          CREATE
    // ===============================================================

    @Test
    @DisplayName("Debe crear un cliente válido cuando el email no existe")
    void debeCrearClienteSiEmailNoExiste() {
        Cliente nuevo = new Cliente();
        nuevo.setNombre("Ana");
        nuevo.setEmail("nuevo@test.com");
        nuevo.setTelefono("123");

        when(clienteRepository.findByEmail("nuevo@test.com")).thenReturn(Optional.empty());
        when(clienteRepository.save(nuevo)).thenReturn(nuevo);

        Cliente resultado = clienteService.registrarCliente(nuevo);

        assertThat(resultado).isNotNull();
        verify(clienteRepository).save(nuevo);
    }

    @Test
    @DisplayName("Debe lanzar EntityExistsException cuando ya existe un email registrado")
    void debeLanzarExcepcionSiEmailYaExiste() {
        Cliente existente = new Cliente();
        existente.setEmail("duplicado@test.com");

        when(clienteRepository.findByEmail("duplicado@test.com"))
                .thenReturn(Optional.of(existente));

        Cliente nuevo = new Cliente();
        nuevo.setEmail("duplicado@test.com");

        assertThatThrownBy(() -> clienteService.registrarCliente(nuevo))
                .isInstanceOf(EntityExistsException.class)
                .hasMessageContaining("Ya existe");

        verify(clienteRepository, never()).save(any());
    }

    // ===============================================================
    //                          READ
    // ===============================================================

    @Test
    @DisplayName("Debe obtener un cliente existente por su ID")
    void debeObtenerClientePorId() {
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombre("Juan");

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        Cliente resultado = clienteService.obtenerClientePorId(1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Juan");
        verify(clienteRepository).findById(1);
    }

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException si el cliente no existe al buscar por ID")
    void debeLanzarExcepcionSiClienteNoExiste() {
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.obtenerClientePorId(99))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("no encontrado");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el ID es inválido al buscar cliente")
    void debeLanzarExcepcionPorIdInvalido() {

        assertThatThrownBy(() -> clienteService.obtenerClientePorId(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mayor que cero");

        verify(clienteRepository, never()).findById(any());
    }

    // ===============================================================
    //                          UPDATE
    // ===============================================================

    @Test
    @DisplayName("Debe actualizar exitosamente cuando findByEmail devuelve vacío (sin conflicto)")
    void debeActualizarCuandoNoHayConflictoEnEmail() {
        Cliente existente = new Cliente("Pedro", "pedro@test.com", "111");
        Cliente cambios = new Cliente("Pedro X", "nuevo@test.com", "222");

        when(clienteRepository.findById(1)).thenReturn(Optional.of(existente));
        when(clienteRepository.findByEmail("nuevo@test.com")).thenReturn(Optional.empty());
        when(clienteRepository.save(any())).thenReturn(existente);

        Cliente actualizado = clienteService.actualizarCliente(1, cambios);

        assertThat(actualizado.getNombre()).isEqualTo("Pedro X");
        assertThat(actualizado.getTelefono()).isEqualTo("222");
        verify(clienteRepository).save(any());
    }

    @Test
    @DisplayName("Debe lanzar NullPointerException si el cliente enviado es null")
    void debeLanzarExcepcionSiClienteNull() {
        assertThatThrownBy(() -> clienteService.actualizarCliente(1, null))
                .isInstanceOf(NullPointerException.class);
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar NullPointerException si el email es null")
    void debeLanzarExcepcionSiEmailNull() {
        Cliente existente = new Cliente("Pedro", "111", "pedro@test.com");
        Cliente cambios = new Cliente();
        cambios.setNombre("Nuevo");
        cambios.setEmail(null);

        when(clienteRepository.findById(1)).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> clienteService.actualizarCliente(1, cambios))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("El email no puede ser null");
    }

    @Test
    @DisplayName("Debe lanzar EntityExistsException si el email pertenece a otro cliente")
    void debeLanzarExcepcionSiEmailDeOtroCliente() {
        Cliente existente = new Cliente();
        existente.setId(1);
        existente.setNombre("Pedro");
        existente.setEmail("igual@test.com");
        existente.setTelefono("111");

        Cliente otro = new Cliente();
        otro.setId(2);
        otro.setNombre("Juan");
        otro.setEmail("duplicado@test.com");
        otro.setTelefono("222");

        Cliente cambios = new Cliente();
        cambios.setNombre("X");
        cambios.setEmail("duplicado@test.com");

        when(clienteRepository.findById(1)).thenReturn(Optional.of(existente));
        when(clienteRepository.findByEmail("duplicado@test.com")).thenReturn(Optional.of(otro));

        assertThatThrownBy(() -> clienteService.actualizarCliente(1, cambios))
                .isInstanceOf(EntityExistsException.class)
                .hasMessageContaining("Ya existe un cliente con el email");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("No debe lanzar excepción si el email pertenece al mismo cliente")
    void noDebeLanzarExcepcionSiEmailEsDelMismoCliente() {

        Cliente existente = new Cliente();
        existente.setId(1);
        existente.setNombre("Pedro");
        existente.setEmail("igual@test.com");
        existente.setTelefono("111");

        Cliente cambios = new Cliente();
        cambios.setNombre("Nuevo");
        cambios.setEmail("igual@test.com");

        when(clienteRepository.findById(1)).thenReturn(Optional.of(existente));
        when(clienteRepository.findByEmail("igual@test.com"))
                .thenReturn(Optional.of(existente));  // MISMO CLIENTE

        clienteService.actualizarCliente(1, cambios);

        verify(clienteRepository).save(any());
    }

    // ===============================================================
    //                          DELETE
    // ===============================================================

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
    @DisplayName("Debe lanzar EntityNotFoundException al eliminar cliente inexistente")
    void debeLanzarExcepcionAlEliminarClienteInexistente() {
        when(clienteRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.eliminarCliente(5))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("no encontrado");

        verify(clienteRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el ID de eliminación es inválido")
    void debeLanzarExcepcionPorIdInvalidoEnDelete() {

        assertThatThrownBy(() -> clienteService.eliminarCliente(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mayor que cero");

        verify(clienteRepository, never()).delete(any());
    }

    // ===============================================================
    //                          LIST
    // ===============================================================

    @Test
    @DisplayName("Debe listar todos los clientes")
    void debeListarTodosLosClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of(new Cliente(), new Cliente()));

        List<Cliente> clientes = clienteService.listarClientes();

        assertThat(clientes).hasSize(2);
        verify(clienteRepository).findAll();
    }
}
