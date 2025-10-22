package gm.tienda_libros.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gm.tienda_libros.DTOs.VentaDTO;
import gm.tienda_libros.controller.VentaController;
import gm.tienda_libros.exception.GlobalExceptionHandler;
import gm.tienda_libros.model.Venta;
import gm.tienda_libros.service.imp.VentaService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VentaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VentaService ventaService;

    @InjectMocks
    private VentaController ventaController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(ventaController)
                                .setControllerAdvice(new GlobalExceptionHandler()) // opcional, si manejas excepciones globales
                                .build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    // ---------- CREAR ----------
    @Test
    @DisplayName("POST /api/ventas -> 201 venta válida")
    void crearVentaValida() throws Exception {
        Venta venta = new Venta();
        venta.setId(1);
        venta.setCodigo("V001");
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(new BigDecimal("150.50"));
        venta.setCodMoneda("USD");
        venta.setIdCliente(1);

        when(ventaService.crearVenta(any(Venta.class))).thenReturn(venta);

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/ventas/1"))
                .andExpect(jsonPath("$.codigo").value("V001"))
                .andExpect(jsonPath("$.total").value(150.50));
    }

    @Test
    @DisplayName("POST /api/ventas -> 400 si faltan campos")
    void crearVentaConCamposFaltantes() throws Exception {
        String json = """
            {"codigo": "V002", "total": 150.50, "codMoneda": "USD"}
        """;

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/ventas -> 409 venta duplicada")
    void crearVentaDuplicada() throws Exception {
        Venta venta = new Venta();
        venta.setCodigo("V001");
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(new BigDecimal("100.00"));
        venta.setCodMoneda("USD");
        venta.setIdCliente(1);

        when(ventaService.crearVenta(any(Venta.class)))
                .thenThrow(new EntityExistsException("Ya existe una venta registrada con ese código"));

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ya existe")));
    }

    // ---------- UPDATE ----------
    @Test
    @DisplayName("PUT /api/ventas/{codigo} -> 200 actualizar venta existente")
    void actualizarVentaExistente() throws Exception {
        String codigo = "V001";
        Venta ventaActualizada = new Venta();
        ventaActualizada.setId(1);
        ventaActualizada.setCodigo(codigo);
        ventaActualizada.setFecha(LocalDateTime.now());
        ventaActualizada.setTotal(new BigDecimal("999.99"));
        ventaActualizada.setCodMoneda("USD");
        ventaActualizada.setIdCliente(1);

        when(ventaService.actualizarVenta(eq(codigo), any(Venta.class))).thenReturn(ventaActualizada);

        mockMvc.perform(put("/api/ventas/{codigo}", codigo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ventaActualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("V001"))
                .andExpect(jsonPath("$.total").value(999.99))
                .andExpect(jsonPath("$.codMoneda").value("USD"));

        verify(ventaService).actualizarVenta(eq(codigo), any(Venta.class));
    }

    @Test
    @DisplayName("PUT /api/ventas/{codigo} -> 404 venta inexistente")
    void actualizarVentaInexistente() throws Exception {
        String codigo = "NO_EXISTE";
        Venta venta = new Venta();
        venta.setCodigo(codigo);
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(new BigDecimal("500.00"));
        venta.setCodMoneda("USD");
        venta.setIdCliente(1);

        when(ventaService.actualizarVenta(eq(codigo), any(Venta.class)))
                .thenThrow(new EntityNotFoundException("Venta no encontrada con código: " + codigo));

        mockMvc.perform(put("/api/ventas/{codigo}", codigo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no encontrada")));

        verify(ventaService).actualizarVenta(eq(codigo), any(Venta.class));
    }

    // ---------- LISTAR ----------
    @Test
    @DisplayName("GET /api/ventas -> 200 lista de ventas")
    void listarVentas() throws Exception {
        VentaDTO v1 = new VentaDTO(1, "V001", LocalDateTime.now(), new BigDecimal("100.00"), "USD", 1, "Juan");
        VentaDTO v2 = new VentaDTO(2, "V002", LocalDateTime.now(), new BigDecimal("200.00"), "USD", 2, "María");

        when(ventaService.listarVentas()).thenReturn(List.of(v1, v2));

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombreCliente").value("Juan"))
                .andExpect(jsonPath("$[1].nombreCliente").value("María"));
    }

    @Test
    @DisplayName("GET /api/ventas -> 200 lista vacía")
    void listarVentasVacia() throws Exception {
        when(ventaService.listarVentas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------- FIND BY CODIGO ----------
    @Test
    @DisplayName("GET /api/ventas/{codigo} -> 200 venta existente")
    void ventaPorCodigoExistente() throws Exception {
        Venta venta = new Venta();
        venta.setId(1);
        venta.setCodigo("V001");
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(new BigDecimal("250.50"));
        venta.setCodMoneda("USD");
        venta.setIdCliente(1);

        when(ventaService.obtenerVentaByCodigo("V001")).thenReturn(venta);

        mockMvc.perform(get("/api/ventas/V001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("V001"))
                .andExpect(jsonPath("$.total").value(250.50));

        verify(ventaService).obtenerVentaByCodigo("V001");
    }

    @Test
    @DisplayName("GET /api/ventas/{codigo} -> 404 venta inexistente")
    void ventaPorCodigoInexistente() throws Exception {
        when(ventaService.obtenerVentaByCodigo("NO_EXISTE"))
                .thenThrow(new EntityNotFoundException("Venta no encontrada con código: NO_EXISTE"));

        mockMvc.perform(get("/api/ventas/NO_EXISTE"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no encontrada")));

        verify(ventaService).obtenerVentaByCodigo("NO_EXISTE");
    }

    // ---------- ELIMINAR ----------
    @Test
    @DisplayName("DELETE /api/ventas/{codigo} -> 204 venta existente")
    void eliminarVentaExistente() throws Exception {
        doNothing().when(ventaService).eliminarVenta("V001");

        mockMvc.perform(delete("/api/ventas/V001"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/ventas/{codigo} -> 404 venta inexistente")
    void eliminarVentaInexistente() throws Exception {
        doThrow(new EntityNotFoundException("Venta no encontrada con código: NO_EXISTE"))
                .when(ventaService).eliminarVenta("NO_EXISTE");

        mockMvc.perform(delete("/api/ventas/NO_EXISTE"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Venta no encontrada")));
    }
}
