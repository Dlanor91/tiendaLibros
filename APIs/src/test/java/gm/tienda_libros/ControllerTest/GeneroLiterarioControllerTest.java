package gm.tienda_libros.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import gm.tienda_libros.model.GeneroLiterario;
import gm.tienda_libros.service.IGeneroLiterarioService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GeneroLiterarioControllerTest.GeneroLiterarioController.class)
public class GeneroLiterarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private IGeneroLiterarioService service;

    @Autowired
    private ObjectMapper objectMapper;

    private GeneroLiterario g1;

    @BeforeEach
    void setup() {
        g1 = new GeneroLiterario();
        g1.setId(1);
        g1.setNombre("Ficción");
        g1.setCodigo("F01");
    }

    @Test
    @DisplayName("GET /api/generos -> 200 y lista de generos")
    void getAllDevuelve200YLista() throws Exception {
        when(service.listarGenerosLiterarios()).thenReturn(Arrays.asList(g1));

        mockMvc.perform(get("/api/generos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].codigo").value("F01"))
                .andExpect(jsonPath("$.[0].nombre").value("Ficción"));

        verify(service, times(1)).listarGenerosLiterarios();
    }

    @Test
    @DisplayName("GET /api/generos/{codigo} -> 200 y genero (existe)")
    void getByCodigoDevuelve200() throws Exception {
        when(service.obtenerGeneroLiterarioByCodigo("F01")).thenReturn(g1);

        mockMvc.perform(get("/api/generos/{codigo}", "F01").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("F01"))
                .andExpect(jsonPath("$.nombre").value("Ficción"));

        verify(service).obtenerGeneroLiterarioByCodigo("F01");
    }

    @Test
    @DisplayName("GET /api/generos/{codigo} -> 404 cuando no existe")
    void getByCodigoDevuelve404CuandoNoExiste() throws Exception {
        when(service.obtenerGeneroLiterarioByCodigo("NOEX")).thenThrow(new EntityNotFoundException("no encontrado"));

        mockMvc.perform(get("/api/generos/{codigo}", "NOEX").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).obtenerGeneroLiterarioByCodigo("NOEX");
    }

    @Test
    @DisplayName("POST /api/generos -> 201 cuando se crea correctamente")
    void postCrearDevuelve201() throws Exception {
        GeneroLiterario nuevo = new GeneroLiterario();
        nuevo.setNombre("Nuevo");
        nuevo.setCodigo("NEW");

        when(service.agregarGeneroLiterario(any(GeneroLiterario.class))).thenAnswer(inv -> {
            GeneroLiterario arg = inv.getArgument(0);
            arg.setId(10);
            return arg;
        });

        mockMvc.perform(post("/api/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.codigo").value("NEW"));

        verify(service).agregarGeneroLiterario(any(GeneroLiterario.class));
    }

    @Test
    @DisplayName("POST /api/generos -> 409 cuando el código ya existe")
    void postCrearDevuelve409CuandoDuplicado() throws Exception {
        GeneroLiterario nuevo = new GeneroLiterario();
        nuevo.setNombre("Ficción duplicada");
        nuevo.setCodigo("F01");

        when(service.agregarGeneroLiterario(any(GeneroLiterario.class)))
                .thenThrow(new EntityExistsException("ya existe"));

        mockMvc.perform(post("/api/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isConflict());

        verify(service).agregarGeneroLiterario(any(GeneroLiterario.class));
    }

    @Test
    @DisplayName("PUT /api/generos/{codigo} -> 200 cuando actualiza")
    void putActualizarDevuelve200() throws Exception {
        GeneroLiterario actualizado = new GeneroLiterario();
        actualizado.setId(1);
        actualizado.setNombre("Ficción Actualizada");
        actualizado.setCodigo("F01");

        // Ajuste aquí: pasamos el código y el objeto
        when(service.actualizarGeneroLiterario(eq("F01"), any(GeneroLiterario.class)))
                .thenReturn(actualizado);

        mockMvc.perform(put("/api/generos/{codigo}", "F01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ficción Actualizada"));

        // Verificación también con el código
        verify(service).actualizarGeneroLiterario(eq("F01"), any(GeneroLiterario.class));
    }


    @Test
    @DisplayName("DELETE /api/generos/{codigo} -> 204 cuando elimina")
    void deleteDevuelve204CuandoElimina() throws Exception {
        doNothing().when(service).eliminarGeneroLiterario("F01");

        mockMvc.perform(delete("/api/generos/{codigo}", "F01"))
                .andExpect(status().isNoContent());

        verify(service).eliminarGeneroLiterario("F01");
    }

    @Test
    @DisplayName("DELETE /api/generos/{codigo} -> 404 cuando no existe")
    void deleteDevuelve404CuandoNoExiste() throws Exception {
        doThrow(new EntityNotFoundException("no existe")).when(service).eliminarGeneroLiterario("NOEX");

        mockMvc.perform(delete("/api/generos/{codigo}", "NOEX"))
                .andExpect(status().isNotFound());

        verify(service).eliminarGeneroLiterario("NOEX");
    }

    /* ------------------ Controlador mínimo usado para las pruebas ------------------
       Si ya tenés un controller real, no hace falta esta clase; la dejé aquí de
       manera local para que el test sea autocontenido y muestre claramente
       la contract API esperada (/api/generos)
    */
    @org.springframework.web.bind.annotation.RestController
    @org.springframework.web.bind.annotation.RequestMapping("/api/generos")
    static class GeneroLiterarioController {

        private final IGeneroLiterarioService service;

        public GeneroLiterarioController(IGeneroLiterarioService service) {
            this.service = service;
        }

        @org.springframework.web.bind.annotation.GetMapping
        public java.util.List<GeneroLiterario> listar() {
            return service.listarGenerosLiterarios();
        }

        @org.springframework.web.bind.annotation.GetMapping("/{codigo}")
        public org.springframework.http.ResponseEntity<?> obtener(@org.springframework.web.bind.annotation.PathVariable String codigo) {
            try {
                GeneroLiterario g = service.obtenerGeneroLiterarioByCodigo(codigo);
                return org.springframework.http.ResponseEntity.ok(g);
            } catch (EntityNotFoundException ex) {
                return org.springframework.http.ResponseEntity.notFound().build();
            }
        }

        @org.springframework.web.bind.annotation.PostMapping
        public org.springframework.http.ResponseEntity<?> crear(@org.springframework.web.bind.annotation.RequestBody GeneroLiterario g) {
            try {
                GeneroLiterario saved = service.agregarGeneroLiterario(g);
                return org.springframework.http.ResponseEntity.status(201).body(saved);
            } catch (EntityExistsException ex) {
                return org.springframework.http.ResponseEntity.status(409).build();
            }
        }

        @org.springframework.web.bind.annotation.PutMapping("/{codigo}")
        public org.springframework.http.ResponseEntity<?> actualizar(@org.springframework.web.bind.annotation.PathVariable String codigo,
                                                                     @org.springframework.web.bind.annotation.RequestBody GeneroLiterario g) {
            // simplificación: delega la actualización al servicio usando el body
            GeneroLiterario saved = service.actualizarGeneroLiterario(codigo, g);
            return org.springframework.http.ResponseEntity.ok(saved);
        }

        @org.springframework.web.bind.annotation.DeleteMapping("/{codigo}")
        public org.springframework.http.ResponseEntity<?> eliminar(@org.springframework.web.bind.annotation.PathVariable String codigo) {
            try {
                service.eliminarGeneroLiterario(codigo);
                return org.springframework.http.ResponseEntity.noContent().build();
            } catch (EntityNotFoundException ex) {
                return org.springframework.http.ResponseEntity.notFound().build();
            }
        }
    }
}
