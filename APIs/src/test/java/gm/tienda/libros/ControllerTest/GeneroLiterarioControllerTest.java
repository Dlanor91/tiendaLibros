package gm.tienda.libros.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import gm.tienda.libros.controller.GeneroLiterarioController;
import gm.tienda.libros.exception.GlobalExceptionHandler;
import gm.tienda.libros.model.GeneroLiterario;
import gm.tienda.libros.service.imp.GeneroLiterarioService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GeneroLiterarioControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private GeneroLiterarioService generoService;

    @InjectMocks
    private GeneroLiterarioController generoController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(generoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    // ---------- CREAR ----------
    @Test
    @DisplayName("POST /api/generosLiterarios -> 201 genero válido")
    void crearGeneroValido() throws Exception {
        GeneroLiterario generoGuardado = new GeneroLiterario("Ficción", "FIC");

        when(generoService.agregarGeneroLiterario(any(GeneroLiterario.class))).thenReturn(generoGuardado);

        String json = objectMapper.writeValueAsString(generoGuardado);

        mockMvc.perform(post("/api/generosLiterarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/generosLiterarios/FIC"))
                .andExpect(jsonPath("$.nombre").value("Ficción"))
                .andExpect(jsonPath("$.codigo").value("FIC"));
    }

    @Test
    @DisplayName("POST /api/generosLiterarios -> 409 si codigo duplicado")
    void crearGeneroDuplicado() throws Exception {
        GeneroLiterario genero = new GeneroLiterario("Ficción", "FIC");

        when(generoService.agregarGeneroLiterario(any(GeneroLiterario.class)))
                .thenThrow(new EntityExistsException("Ya existe un genero con el codigo: " + genero.getCodigo()));

        mockMvc.perform(post("/api/generosLiterarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genero)))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ya existe")));
    }

    // ---------- ACTUALIZAR ----------
    @Test
    @DisplayName("PUT /api/generosLiterarios/{codigo} -> 200 genero existente")
    void actualizarGeneroExistente() throws Exception {
        GeneroLiterario cambios = new GeneroLiterario("Ficción Moderna", "FIC");

        when(generoService.actualizarGeneroLiterario(eq("FIC"), any(GeneroLiterario.class)))
                .thenReturn(cambios);

        mockMvc.perform(put("/api/generosLiterarios/FIC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambios)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ficción Moderna"))
                .andExpect(jsonPath("$.codigo").value("FIC"));
    }

    @Test
    @DisplayName("PUT /api/generosLiterarios/{codigo} -> 404 genero inexistente")
    void actualizarGeneroInexistente() throws Exception {
        GeneroLiterario cambios = new GeneroLiterario("Nuevo", "ZZZ");

        when(generoService.actualizarGeneroLiterario(eq("ZZZ"), any(GeneroLiterario.class)))
                .thenThrow(new EntityNotFoundException("Genero no encontrado con codigo: ZZZ"));

        mockMvc.perform(put("/api/generosLiterarios/ZZZ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambios)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no encontrado")));
    }

    // ---------- OBTENER ----------
    @Test
    @DisplayName("GET /api/generosLiterarios/{codigo} -> 200 genero existente")
    void obtenerGeneroExistente() throws Exception {
        GeneroLiterario genero = new GeneroLiterario("Ficción", "FIC");

        when(generoService.obtenerGeneroLiterarioByCodigo("FIC")).thenReturn(genero);

        mockMvc.perform(get("/api/generosLiterarios/FIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ficción"))
                .andExpect(jsonPath("$.codigo").value("FIC"));
    }

    @Test
    @DisplayName("GET /api/generosLiterarios/{codigo} -> 404 genero inexistente")
    void obtenerGeneroInexistente() throws Exception {
        when(generoService.obtenerGeneroLiterarioByCodigo("ZZZ"))
                .thenThrow(new EntityNotFoundException("Genero no encontrado con codigo: ZZZ"));

        mockMvc.perform(get("/api/generosLiterarios/ZZZ"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no encontrado")));
    }

    // ---------- ELIMINAR ----------
    @Test
    @DisplayName("DELETE /api/generosLiterarios/{codigo} -> 204 genero existente")
    void eliminarGeneroExistente() throws Exception {
        doNothing().when(generoService).eliminarGeneroLiterario("FIC");

        mockMvc.perform(delete("/api/generosLiterarios/FIC"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/generosLiterarios/{codigo} -> 404 genero inexistente")
    void eliminarGeneroInexistente() throws Exception {
        doThrow(new EntityNotFoundException("Genero no encontrado con codigo: ZZZ"))
                .when(generoService).eliminarGeneroLiterario("ZZZ");

        mockMvc.perform(delete("/api/generosLiterarios/ZZZ"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no encontrado")));
    }

    // ---------- LISTAR ----------
    @Test
    @DisplayName("GET /api/generosLiterarios -> 200 lista de generos")
    void listarGeneros() throws Exception {
        List<GeneroLiterario> generos = List.of(
                new GeneroLiterario("Ficción", "FIC"),
                new GeneroLiterario("Poesía", "POE")
        );

        when(generoService.listarGenerosLiterarios()).thenReturn(generos);

        mockMvc.perform(get("/api/generosLiterarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Ficción"))
                .andExpect(jsonPath("$[1].nombre").value("Poesía"));
    }

    @Test
    @DisplayName("GET /api/generosLiterarios -> 200 lista vacía")
    void listarGenerosVacia() throws Exception {
        when(generoService.listarGenerosLiterarios()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/generosLiterarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
