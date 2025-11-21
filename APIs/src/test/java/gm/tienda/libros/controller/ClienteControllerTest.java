package gm.tienda.libros.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gm.tienda.libros.exception.GlobalExceptionHandler;
import gm.tienda.libros.model.Cliente;
import gm.tienda.libros.service.imp.ClienteService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    // ---------- CREAR ----------
    @Test
    @DisplayName("POST /api/clientes -> 201 cliente válido")
    void crearClienteValido() throws Exception {
        Cliente clienteGuardado = new Cliente("Juan","juan@test.com","099123456");
        clienteGuardado.setId(1);

        when(clienteService.registrarCliente(any(Cliente.class))).thenReturn(clienteGuardado);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteGuardado)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/clientes/1"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    @DisplayName("POST /api/clientes -> 400 si falta email")
    void crearClienteSinEmail() throws Exception {
        String json = """
            {"nombre": "Juan", "telefono": "099123456"}
        """;

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/clientes -> 409 cliente duplicado")
    void crearClienteDuplicado() throws Exception {
        Cliente cliente = new Cliente("Juan","juan@test.com","099123456");
        when(clienteService.registrarCliente(any(Cliente.class)))
                .thenThrow(new EntityExistsException("Ya existe un cliente con el email: " + cliente.getEmail()));

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ya existe")));
    }

    // ---------- ACTUALIZAR ----------
    @Test
    @DisplayName("PUT /api/clientes/{id} -> 200 cliente existente")
    void actualizarClienteExistente() throws Exception {
        Cliente cambios = new Cliente("Juan Actualizado","juan@test.com","099654321");

        when(clienteService.actualizarCliente(eq(1), any(Cliente.class))).thenReturn(cambios);

        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambios)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Actualizado"))
                .andExpect(jsonPath("$.telefono").value("099654321"));
    }

    @Test
    @DisplayName("PUT /api/clientes/{id} -> 404 cliente inexistente")
    void actualizarClienteInexistente() throws Exception {
        Cliente cambios = new Cliente("Juan Actualizado","juan@test.com","099654321");

        when(clienteService.actualizarCliente(eq(99), any(Cliente.class)))
                .thenThrow(new EntityNotFoundException("Cliente no encontrado con ID: 99"));

        mockMvc.perform(put("/api/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambios)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no encontrado")));
    }

    @Test
    @DisplayName("PUT /api/clientes/{id} -> 409 email existente")
    void actualizarClienteEmailDuplicado() throws Exception {
        Cliente cambios = new Cliente("Juan Actualizado","emailexistente@test.com","099654321");

        when(clienteService.actualizarCliente(eq(1), any(Cliente.class)))
                .thenThrow(new EntityExistsException("Ya existe un cliente con el email: emailexistente@test.com"));

        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambios)))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ya existe")));
    }

    // ---------- OBTENER ----------
    @Test
    @DisplayName("GET /api/clientes/{id} -> 200 cliente existente")
    void obtenerClienteExistente() throws Exception {
        Cliente cliente = new Cliente("Juan","juan@test.com","099123456");
        cliente.setId(1);

        when(clienteService.obtenerClientePorId(1)).thenReturn(cliente);

        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    @DisplayName("GET /api/clientes/{id} -> 404 cliente inexistente")
    void obtenerClienteInexistente() throws Exception {
        when(clienteService.obtenerClientePorId(99))
                .thenThrow(new EntityNotFoundException("Cliente no encontrado con ID: 99"));

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no encontrado")));
    }

    // ---------- ELIMINAR ----------
    @Test
    @DisplayName("DELETE /api/clientes/{id} -> 204 cliente existente")
    void eliminarClienteExistente() throws Exception {
        doNothing().when(clienteService).eliminarCliente(1);

        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/clientes/{id} -> 404 cliente inexistente")
    void eliminarClienteInexistente() throws Exception {
        doThrow(new EntityNotFoundException("Cliente no encontrado con ID: 99"))
                .when(clienteService).eliminarCliente(99);

        mockMvc.perform(delete("/api/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no encontrado")));
    }

    // ---------- LISTAR ----------
    @Test
    @DisplayName("GET /api/clientes -> 200 lista de clientes")
    void listarClientes() throws Exception {
        List<Cliente> clientes = List.of(
                new Cliente("Juan","juan@test.com","099123456"),
                new Cliente("Ana","ana@test.com","099654321")
        );

        when(clienteService.listarClientes()).thenReturn(clientes);

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                .andExpect(jsonPath("$[1].nombre").value("Ana"));
    }

    @Test
    @DisplayName("GET /api/clientes -> 200 lista vacía")
    void listarClientesVacio() throws Exception {
        when(clienteService.listarClientes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/clientes/{id} -> 500 error inesperado (handler general)")
    void manejarExcepcionGeneral() throws Exception {

        when(clienteService.obtenerClientePorId(1))
                .thenThrow(new RuntimeException("Fallo inesperado en el servicio"));

        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error inesperado")));
    }

    @Test
    @DisplayName("PUT /api/clientes/{id} -> 400 IllegalArgumentException")
    void actualizarClienteIllegalArgument() throws Exception {

        when(clienteService.actualizarCliente(eq(1), any(Cliente.class)))
                .thenThrow(new IllegalArgumentException("Datos inválidos"));

        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"nombre": "Juan", "email": "juan@test.com", "telefono": "099123456"}
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Datos inválidos")));
    }
}
