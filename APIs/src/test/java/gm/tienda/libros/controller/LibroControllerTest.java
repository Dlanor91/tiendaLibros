package gm.tienda.libros.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gm.tienda.libros.dto.LibroDTO;
import gm.tienda.libros.dto.LibroRequestDTO;
import gm.tienda.libros.exception.GlobalExceptionHandler;
import gm.tienda.libros.service.imp.LibroService;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LibroControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private LibroService libroService;

    @InjectMocks
    private LibroController libroController;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(libroController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ----------------------------------------------------------
    //                      HELPERS UNIFICADOS
    // ----------------------------------------------------------

    private LibroRequestDTO crearRequest() {
        return new LibroRequestDTO(
                null,
                "ISBN123",
                "Cien Años de Soledad",
                "USD",
                new BigDecimal("25.00"),
                100,
                "Descripción",
                LocalDate.of(1967, 5, 5),
                "FIC",
                List.of(1,2)
        );
    }

    private LibroDTO crearEntidad() {
        return new LibroDTO(
                "ISBN123",
                "Cien Años de Soledad",
                "USD",
                new BigDecimal("25.00"),
                100,
                "Descripción",
                LocalDate.of(1967, 5, 5),
                "FIC",
                "Ficción",
                List.of()
        );
    }

    // ----------------------------------------------------------
    //                       CREAR LIBRO
    // ----------------------------------------------------------
    @Test
    @DisplayName("POST /api/libros -> 201 libro creado")
    void crearLibroValido() throws Exception {

        LibroRequestDTO request = crearRequest();
        LibroDTO respuesta = crearEntidad();

        when(libroService.insertarLibro(any())).thenReturn(respuesta);

        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/libros/ISBN123"))
                .andExpect(jsonPath("$.isbn").value("ISBN123"))
                .andExpect(jsonPath("$.nombre").value("Cien Años de Soledad"));
    }

    @Test
    @DisplayName("POST /api/libros -> 400 libro inválido")
    void crearLibroInvalido() throws Exception {
        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"sin isbn\"}"))
                .andExpect(status().isBadRequest());
    }

    // ----------------------------------------------------------
    //                    ACTUALIZAR LIBRO
    // ----------------------------------------------------------
    @Test
    @DisplayName("PUT /api/libros/{isbn} -> 200 libro actualizado")
    void actualizarLibroExistente() throws Exception {

        LibroRequestDTO cambios = crearRequest();

        LibroDTO actualizado = new LibroDTO(
                "ISBN123",
                "Edición Revisada",
                "USD",
                new BigDecimal("10"),
                5,
                "Desc",
                LocalDate.now(),
                "FIC",
                "Ficción",
                List.of()
        );

        when(libroService.actualizarLibro(eq("ISBN123"), any()))
                .thenReturn(actualizado);

        mockMvc.perform(put("/api/libros/ISBN123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambios)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Edición Revisada"));
    }

    @Test
    @DisplayName("PUT /api/libros/{isbn} -> 404 inexistente")
    void actualizar404() throws Exception {

        when(libroService.actualizarLibro(eq("NOEXISTE"), any()))
                .thenThrow(new EntityNotFoundException("Libro no encontrado"));

        mockMvc.perform(put("/api/libros/NOEXISTE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRequest())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("no encontrado")));
    }

    // ----------------------------------------------------------
    //                     OBTENER POR ISBN
    // ----------------------------------------------------------
    @Test
    @DisplayName("GET /api/libros/{isbn} -> 200")
    void obtenerLibro() throws Exception {

        when(libroService.buscarLibroPorIsbn("ISBN123"))
                .thenReturn(crearEntidad());

        mockMvc.perform(get("/api/libros/ISBN123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("ISBN123"));
    }

    @Test
    @DisplayName("GET /api/libros/{isbn} -> 404")
    void obtener404() throws Exception {

        when(libroService.buscarLibroPorIsbn("XXX"))
                .thenThrow(new EntityNotFoundException("Libro no encontrado"));

        mockMvc.perform(get("/api/libros/XXX"))
                .andExpect(status().isNotFound());
    }

    // ----------------------------------------------------------
    //                       ELIMINAR
    // ----------------------------------------------------------
    @Test
    @DisplayName("DELETE /api/libros/{isbn} -> 204")
    void eliminar200() throws Exception {

        doNothing().when(libroService).eliminarLibro("ISBN123");

        mockMvc.perform(delete("/api/libros/ISBN123"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/libros/{isbn} -> 404")
    void eliminar404() throws Exception {

        doThrow(new EntityNotFoundException("Libro no encontrado"))
                .when(libroService).eliminarLibro("XXX");

        mockMvc.perform(delete("/api/libros/XXX"))
                .andExpect(status().isNotFound());
    }

    // ----------------------------------------------------------
    //                       LISTAR
    // ----------------------------------------------------------
    @Test
    @DisplayName("GET /api/libros -> lista llena")
    void listar() throws Exception {

        when(libroService.listarLibros())
                .thenReturn(List.of(crearEntidad()));

        mockMvc.perform(get("/api/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/libros -> lista vacía")
    void listarVacio() throws Exception {

        when(libroService.listarLibros()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
