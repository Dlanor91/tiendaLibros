package gm.tienda_libros.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import gm.tienda_libros.controller.ClienteController;
import gm.tienda_libros.model.Cliente;
import gm.tienda_libros.service.imp.ClienteService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private ClienteService clienteService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    //---------- Crear ----------//
    @Test
    void debeRetornar201AlCrearClienteValido() throws Exception {
        String json = """
            {"nombre": "Juan", "email": "juan@test.com", "telefono": "099123456"}
        """;

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void debeRetornar400SiFaltaEmail() throws Exception {
        String json = """
            {"nombre": "Juan", "telefono": "099123456"}
        """;

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar409SiEmailDuplicado() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setEmail("juan@test.com");
        cliente.setTelefono("099123456");

        when(clienteService.registrarCliente(any(Cliente.class)))
                .thenThrow(new EntityExistsException("Ya existe un cliente con el email: " + cliente.getEmail()));

        String json = objectMapper.writeValueAsString(cliente);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ya existe")));
    }

    //---------- Update ----------//
    @Test
    void debeRetornar404AlActualizarClienteInexistente() throws Exception {
        Cliente cambios = new Cliente();
        cambios.setNombre("Juan Actualizado");
        cambios.setEmail("juan@test.com");
        cambios.setTelefono("099654321");

        when(clienteService.actualizarCliente(eq(99), any(Cliente.class)))
                .thenThrow(new EntityNotFoundException("Cliente no encontrado con ID: 99"));

        String json = objectMapper.writeValueAsString(cambios);

        mockMvc.perform(put("/api/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no encontrado")));
    }

    @Test
    void debeRetornar200AlActualizarClienteExistente() throws Exception {
        Cliente cambios = new Cliente();
        cambios.setNombre("Juan Actualizado");
        cambios.setEmail("juan@test.com");
        cambios.setTelefono("099654321");

        when(clienteService.actualizarCliente(eq(1), any(Cliente.class)))
                .thenReturn(cambios);

        String json = objectMapper.writeValueAsString(cambios);

        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Actualizado"))
                .andExpect(jsonPath("$.telefono").value("099654321"));
    }

    //---------- Find By Id ----------//
    @Test
    void debeRetornar200AlObtenerClienteExistente() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombre("Juan");
        cliente.setEmail("juan@test.com");
        cliente.setTelefono("099123456");

        when(clienteService.obtenerClientePorId(1)).thenReturn(cliente);

        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    void debeRetornar404AlObtenerClienteInexistente() throws Exception {
        when(clienteService.obtenerClientePorId(99))
                .thenThrow(new EntityNotFoundException("Cliente no encontrado con ID: 99"));

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no encontrado")));
    }

    //---------- Eliminar ----------//
    @Test
    void debeRetornar204AlEliminarClienteExistente() throws Exception {
        doNothing().when(clienteService).eliminarCliente(1);

        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void debeRetornar404AlEliminarClienteInexistente() throws Exception {
        doThrow(new EntityNotFoundException("Cliente no encontrado con ID: 99"))
                .when(clienteService).eliminarCliente(99);

        mockMvc.perform(delete("/api/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no encontrado")));
    }

    //---------- Listar ----------//
    @Test
    void debeRetornar200YListaDeClientes() throws Exception {
        List<Cliente> clientes = List.of(
                new Cliente("Juan", "juan@test.com", "099123456"),
                new Cliente("Ana", "ana@test.com", "099654321")
        );

        when(clienteService.listarClientes()).thenReturn(clientes);

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                .andExpect(jsonPath("$[1].nombre").value("Ana"));
    }

    @Test
    void debeRetornar200YListaVaciaSiNoHayClientes() throws Exception {
        when(clienteService.listarClientes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
