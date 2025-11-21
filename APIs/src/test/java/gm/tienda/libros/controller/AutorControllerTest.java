package gm.tienda.libros.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gm.tienda.libros.dto.AutorDTO;
import gm.tienda.libros.dto.AutorRequestDTO;
import gm.tienda.libros.exception.GlobalExceptionHandler;
import gm.tienda.libros.model.Autor;
import gm.tienda.libros.service.imp.AutorService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AutorControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AutorService autorService;

    @InjectMocks
    private AutorController autorController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(autorController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }


    // ----------------------------------------------------------
    //                       CREAR AUTOR
    // ----------------------------------------------------------
    @Test
    @DisplayName("POST /api/autores -> 201 autor válido")
    void crearAutorValido() throws Exception {

        AutorRequestDTO request = new AutorRequestDTO(
                null,
                "Gabriel",
                "García Márquez"
        );

        Autor respuesta = new Autor();
        respuesta.setId(1);
        respuesta.setNombre("Gabriel");
        respuesta.setApellidos("García Márquez");

        when(autorService.crearAutor(any(AutorRequestDTO.class))).thenReturn(respuesta);

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/autores/1"))
                .andExpect(jsonPath("$.nombre").value("Gabriel"))
                .andExpect(jsonPath("$.apellidos").value("García Márquez"));
    }

    @Test
    @DisplayName("POST /api/autores -> 400 autor sin nombre")
    void crearAutorSinNombre() throws Exception {

        String json = """
            {"id": null, "apellidos": "García Márquez"}
        """;

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/autores -> 409 autor duplicado")
    void crearAutorDuplicado() throws Exception {

        AutorRequestDTO dto = new AutorRequestDTO(
                null,
                "Gabriel",
                "García Márquez"
        );

        when(autorService.crearAutor(any()))
                .thenThrow(new EntityExistsException("El autor ya existe"));

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("existe")));
    }


    // ----------------------------------------------------------
    //                       ACTUALIZAR AUTOR
    // ----------------------------------------------------------
    @Test
    @DisplayName("PUT /api/autores/{id} -> 200 autor actualizado")
    void actualizarAutorExistente() throws Exception {

        AutorRequestDTO cambios = new AutorRequestDTO(
                1,
                "Gabriel",
                "García Editado"
        );

        Autor respuesta = new Autor();
        respuesta.setId(1);
        respuesta.setNombre("Gabriel");
        respuesta.setApellidos("García Editado");

        when(autorService.actualizarAutor(eq(1), any(AutorRequestDTO.class)))
                .thenReturn(respuesta);

        mockMvc.perform(put("/api/autores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambios)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apellidos").value("García Editado"));
    }

    @Test
    @DisplayName("PUT /api/autores/{id} -> 404 autor inexistente")
    void actualizarAutorInexistente() throws Exception {

        AutorRequestDTO cambios = new AutorRequestDTO(
                99,
                "Gabriel",
                "Algo"
        );

        when(autorService.actualizarAutor(eq(99), any()))
                .thenThrow(new EntityNotFoundException("Autor no encontrado"));

        mockMvc.perform(put("/api/autores/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambios)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no encontrado")));
    }

    @Test
    @DisplayName("PUT /api/autores/{id} -> 409 conflicto (autor duplicado)")
    void actualizarAutorDuplicado() throws Exception {

        AutorRequestDTO cambios = new AutorRequestDTO(
                1,
                "Gabriel",
                "García Márquez"
        );

        when(autorService.actualizarAutor(eq(1), any()))
                .thenThrow(new EntityExistsException("Autor existente"));

        mockMvc.perform(put("/api/autores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambios)))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("existente")));
    }

    @Test
    @DisplayName("PUT /api/autores/{id} -> 400 IllegalArgumentException")
    void actualizarAutorIllegalArgument() throws Exception {

        AutorRequestDTO cambios = new AutorRequestDTO(
                1,
                "Gabriel",
                "X"
        );

        when(autorService.actualizarAutor(eq(1), any()))
                .thenThrow(new IllegalArgumentException("Datos inválidos"));

        mockMvc.perform(put("/api/autores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambios)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("inválidos")));
    }


    // ----------------------------------------------------------
    //                 OBTENER AUTOR POR ID
    // ----------------------------------------------------------
    /*
    @Test
    @DisplayName("GET /api/autores/{id} -> 200 autor existente")
    void obtenerAutorExistente() throws Exception {

        Autor autor = new Autor();
        autor.setId(1);
        autor.setNombre("Gabriel");
        autor.setApellidos("García Márquez");

        when(autorService.obtenerAutorPorId(1)).thenReturn(autor);

        mockMvc.perform(get("/api/autores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Gabriel"));
    }

    @Test
    @DisplayName("GET /api/autores/{id} -> 404 autor inexistente")
    void obtenerAutorInexistente() throws Exception {

        when(autorService.obtenerAutorPorId(99))
                .thenThrow(new EntityNotFoundException("Autor no encontrado"));

        mockMvc.perform(get("/api/autores/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no encontrado")));
    }

    @Test
    @DisplayName("GET /api/autores/{id} -> 500 error inesperado")
    void obtenerAutorErrorInesperado() throws Exception {

        when(autorService.obtenerAutorPorId(1))
                .thenThrow(new RuntimeException("Fallo inesperado"));

        mockMvc.perform(get("/api/autores/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error inesperado")));
    }*/

    // ----------------------------------------------------------
    //                       ELIMINAR AUTOR
    // ----------------------------------------------------------

    @Test
    @DisplayName("DELETE /api/autores/{id} -> 204 autor eliminado")
    void eliminarAutorExistente() throws Exception {

        doNothing().when(autorService).eliminarAutor(1);

        mockMvc.perform(delete("/api/autores/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/autores/{id} -> 404 autor inexistente")
    void eliminarAutorInexistente() throws Exception {

        doThrow(new EntityNotFoundException("Autor no encontrado"))
                .when(autorService).eliminarAutor(99);

        mockMvc.perform(delete("/api/autores/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no encontrado")));
    }


    // ----------------------------------------------------------
    //                       LISTAR AUTORES
    // ----------------------------------------------------------

    @Test
    @DisplayName("GET /api/autores -> 200 lista de autores")
    void listarAutores() throws Exception {

        AutorDTO autor = new AutorDTO(1,"Gabriel","García Márquez");

        AutorDTO autorDos = new AutorDTO(2,"Jorge Luis","Borges");

        List<AutorDTO> autores = List.of(autor,autorDos);

        when(autorService.listarAutores()).thenReturn(autores);

        mockMvc.perform(get("/api/autores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Gabriel"));
    }

    @Test
    @DisplayName("GET /api/autores -> 200 lista vacía")
    void listarAutoresVacio() throws Exception {

        when(autorService.listarAutores()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/autores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ----------------------------------------------------------
    //                     BUSCAR POR NOMBRE
    // ----------------------------------------------------------
    /*
    @Test
    @DisplayName("GET /api/autores/buscar?nombre=x -> 200 coincidencias")
    void buscarPorNombre() throws Exception {

        Autor autor = new Autor();
        autor.setNombre("Gabriel");
        autor.setApellidos("García Márquez");

        List<Autor> autores = List.of(autor);

        when(autorService.buscarAutoresNombre("Gabriel")).thenReturn(autores);

        mockMvc.perform(get("/api/autores/buscar")
                        .param("nombre", "Gabriel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Gabriel"))
                .andExpect(jsonPath("$[0].apellidos").value("García Márquez"));
    }*/
}
