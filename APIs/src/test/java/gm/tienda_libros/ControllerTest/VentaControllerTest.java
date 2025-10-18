package gm.tienda_libros.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gm.tienda_libros.DTOs.VentaDTO;
import gm.tienda_libros.controller.VentaController;
import gm.tienda_libros.model.Venta;
import gm.tienda_libros.service.imp.VentaService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VentaController.class)
class VentaControllerTest {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private VentaService ventaService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // ---------- CREAR ----------
    @Test
    @DisplayName("Debe retornar 201 al crear una venta válida")
    void debeRetornar201AlCrearVentaValida() throws Exception {
        Venta venta = new Venta();
        venta.setId(1);
        venta.setCodigo("V001");
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(new BigDecimal("150.50"));
        venta.setCodMoneda("USD");
        venta.setIdCliente(1);

        when(ventaService.crearVenta(any(Venta.class))).thenReturn(venta);

        String json = objectMapper.writeValueAsString(venta);

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/ventas/1"))
                .andExpect(jsonPath("$.codigo").value("V001"))
                .andExpect(jsonPath("$.total").value(150.50));
    }

    @Test
    @DisplayName("Debe retornar 400 si falta campo obligatorio")
    void debeRetornar400SiFaltanCampos() throws Exception {
        String json = """
            {"codigo": "V002", "total": 150.50, "codMoneda": "USD"}
        """; // Falta fecha e idCliente

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe retornar 409 si la venta ya existe")
    void debeRetornar409SiVentaDuplicada() throws Exception {
        Venta venta = new Venta();
        venta.setCodigo("V001");
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(new BigDecimal("100.00"));
        venta.setCodMoneda("USD");
        venta.setIdCliente(1);

        when(ventaService.crearVenta(any(Venta.class)))
                .thenThrow(new EntityExistsException("Ya existe una venta registrada con ese código"));

        String json = objectMapper.writeValueAsString(venta);

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ya existe")));
    }

    // ---------- UPDATE ----------
    @Test
    @DisplayName("Debe retornar 200 al actualizar una venta existente")
    void debeRetornar200AlActualizarVentaExistente() throws Exception {
        String codigo = "V001";

        Venta ventaActualizada = new Venta();
        ventaActualizada.setId(1);
        ventaActualizada.setCodigo(codigo);
        ventaActualizada.setFecha(LocalDateTime.now());
        ventaActualizada.setTotal(new BigDecimal("999.99"));
        ventaActualizada.setCodMoneda("USD");
        ventaActualizada.setIdCliente(1);

        when(ventaService.actualizarVenta(eq(codigo), any(Venta.class))).thenReturn(ventaActualizada);

        String json = objectMapper.writeValueAsString(ventaActualizada);

        mockMvc.perform(put("/api/ventas/{codigo}", codigo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("V001"))
                .andExpect(jsonPath("$.total").value(999.99))
                .andExpect(jsonPath("$.codMoneda").value("USD"));

        verify(ventaService).actualizarVenta(eq(codigo), any(Venta.class));
    }

    @Test
    @DisplayName("Debe retornar 404 si se intenta actualizar una venta inexistente")
    void debeRetornar404SiVentaNoExisteAlActualizar() throws Exception {
        String codigo = "NO_EXISTE";

        Venta venta = new Venta();
        venta.setCodigo(codigo);
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(new BigDecimal("500.00"));
        venta.setCodMoneda("USD");
        venta.setIdCliente(1);

        when(ventaService.actualizarVenta(eq(codigo), any(Venta.class)))
                .thenThrow(new EntityNotFoundException("Venta no encontrada con código: " + codigo));

        String json = objectMapper.writeValueAsString(venta);

        mockMvc.perform(put("/api/ventas/{codigo}", codigo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no encontrada")));

        verify(ventaService).actualizarVenta(eq(codigo), any(Venta.class));
    }

    // ---------- LISTAR ----------
    @Test
    @DisplayName("Debe retornar 200 y lista de ventas con sus clientes")
    void debeRetornar200YListaDeVentasConClientes() throws Exception {
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
    @DisplayName("Debe retornar 200 y lista vacía si no hay ventas")
    void debeRetornar200YListaVacia() throws Exception {
        when(ventaService.listarVentas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------- FIND BY CODIGO ----------
    @Test
    @DisplayName("Debe retornar 200 y la venta correspondiente al código existente")
    void debeRetornar200YVentaPorCodigoExistente() throws Exception {
        Venta venta = new Venta();
        venta.setId(1);
        venta.setCodigo("V001");
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(new BigDecimal("250.50"));
        venta.setCodMoneda("USD");
        venta.setIdCliente(1);

        when(ventaService.obtenerVentaByCodigo("V001")).thenReturn(venta);

        mockMvc.perform(get("/api/ventas/V001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("V001"))
                .andExpect(jsonPath("$.total").value(250.50))
                .andExpect(jsonPath("$.codMoneda").value("USD"));

        verify(ventaService).obtenerVentaByCodigo("V001");
    }

    @Test
    @DisplayName("Debe retornar 404 si no existe una venta con ese código")
    void debeRetornar404SiVentaNoExistePorCodigo() throws Exception {
        when(ventaService.obtenerVentaByCodigo("NO_EXISTE"))
                .thenThrow(new EntityNotFoundException("Venta no encontrada con código: NO_EXISTE"));

        mockMvc.perform(get("/api/ventas/NO_EXISTE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no encontrada")));

        verify(ventaService).obtenerVentaByCodigo("NO_EXISTE");
    }

    // ---------- ELIMINAR ----------
    @Test
    @DisplayName("Debe retornar 204 al eliminar venta existente")
    void debeRetornar204AlEliminarVentaExistente() throws Exception {
        doNothing().when(ventaService).eliminarVenta("asd");

        mockMvc.perform(delete("/api/ventas/asd"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Debe retornar 404 al eliminar venta inexistente")
    void debeRetornar404AlEliminarVentaInexistente() throws Exception {
        doThrow(new EntityNotFoundException("Venta no encontrada con ID: 99"))
                .when(ventaService).eliminarVenta("asd");

        mockMvc.perform(delete("/api/ventas/asd"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Venta no encontrada")));
    }
}
