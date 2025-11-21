package gm.tienda.libros.service;

import gm.tienda.libros.dto.AutorDTO;
import gm.tienda.libros.dto.AutorRequestDTO;
import gm.tienda.libros.model.Autor;
import gm.tienda.libros.repository.AutorRepository;
import gm.tienda.libros.service.imp.AutorService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutorServiceTest {

    @Mock
    private AutorRepository autorRepository;

    @InjectMocks
    private AutorService autorService;

    // ======================================================
    //            PROVEEDORES PARA TEST PARAMETRIZADO
    // ======================================================

    static Stream<Autor> autoresInvalidos() {
        return Stream.of(
                null,
                new Autor() {{ setNombre(null); setApellidos("X"); }},
                new Autor() {{ setNombre(" "); setApellidos("X"); }},
                new Autor() {{ setNombre("Nombre"); setApellidos(null); }},
                new Autor() {{ setNombre("Nombre"); setApellidos(" "); }}
        );
    }

    static Stream<Object[]> idsInvalidos() {
        return Stream.of(
                new Object[]{null},
                new Object[]{0},
                new Object[]{-5}
        );
    }

    // ======================================================
    //                          CREATE
    // ======================================================

    @Test
    @DisplayName("Debe crear un autor correctamente")
    void debeCrearAutor() {
        // DTO de entrada
        AutorRequestDTO dto = new AutorRequestDTO(1,"Gabriel", "García Márquez");

        // Entidad que el repositorio debería retornar
        Autor autorGuardado = new Autor();
        autorGuardado.setId(1);
        autorGuardado.setNombre("Gabriel");
        autorGuardado.setApellidos("García Márquez");

        // Mock del repositorio
        when(autorRepository.save(any(Autor.class))).thenReturn(autorGuardado);

        // Ejecutar servicio
        Autor creado = autorService.crearAutor(dto);

        // Validaciones
        assertThat(creado).isNotNull();
        assertThat(creado.getNombre()).isEqualTo("Gabriel");
        assertThat(creado.getApellidos()).isEqualTo("García Márquez");

        // Verificación de llamada al repositorio (con una entidad Autor)
        verify(autorRepository).save(any(Autor.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el autor es null al crear")
    void debeLanzarExcepcionAlCrearAutorNull() {

        assertThatThrownBy(() -> autorService.crearAutor(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede ser null"); // evita depender solo de "autor"

        verify(autorRepository, never()).save(any());
    }

    // ======================================================
    //                          FIND BY ID
    // ======================================================

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el ID es inválido")
    void debeLanzarExcepcionPorIdInvalido() {
        assertThatThrownBy(() -> autorService.obtenerAutorPorId(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");

        verify(autorRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Debe obtener un autor existente por su ID")
    void debeObtenerAutorPorId() {
        Autor autor = new Autor();
        autor.setId(1);
        autor.setNombre("Isabel");
        autor.setApellidos("Allende");

        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));

        Autor resultado = autorService.obtenerAutorPorId(1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Isabel");
        assertThat(resultado.getApellidos()).isEqualTo("Allende");
        verify(autorRepository).findById(1);
    }

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException cuando el autor no existe")
    void debeLanzarExcepcionSiAutorNoExiste() {
        when(autorRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autorService.obtenerAutorPorId(99))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("se encontró el autor");
    }

    // ======================================================
    //                          UPDATE
    // ======================================================
    @Test
    @DisplayName("Debe actualizar un autor existente correctamente")
    void debeActualizarAutorExistente() {
        Autor existente = new Autor();
        existente.setId(1);
        existente.setNombre("Ernest");
        existente.setApellidos("Hemingway");

        when(autorRepository.findById(1)).thenReturn(Optional.of(existente));
        when(autorRepository.save(existente)).thenReturn(existente);

        AutorRequestDTO cambios = new AutorRequestDTO(1,"Ernest Updated","Hemingway Updated");

        Autor actualizado = autorService.actualizarAutor(1, cambios);

        assertThat(actualizado.getNombre()).isEqualTo("Ernest Updated");
        verify(autorRepository).save(existente);
    }

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException al actualizar un autor inexistente")
    void debeFallarUpdateSiAutorNoExiste() {
        when(autorRepository.findById(10)).thenReturn(Optional.empty());

        AutorRequestDTO cambios = new AutorRequestDTO(10,"Nombre","Apellido");

        assertThatThrownBy(() -> autorService.actualizarAutor(10, cambios))
                .isInstanceOf(EntityNotFoundException.class);

        verify(autorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el autor es null en actualizarAutor")
    void debeLanzarExcepcionPorAutorNullEnActualizar() {

        assertThatThrownBy(() -> autorService.actualizarAutor(1, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("autor");

        verify(autorRepository, never()).findById(any());
    }

    // ======================================================
    //                          DELETE
    // ======================================================

    @ParameterizedTest
    @MethodSource("idsInvalidos")
    @DisplayName("Debe fallar al eliminar cuando el ID es inválido")
    void debeFallarEliminarIdInvalido(Integer id) {

        assertThatThrownBy(() -> autorService.eliminarAutor(id))
                .isInstanceOf(IllegalArgumentException.class);

        verify(autorRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debe eliminar un autor existente")
    void debeEliminarAutorExistente() {
        Autor existente = new Autor();
        existente.setId(1);

        when(autorRepository.findById(1)).thenReturn(Optional.of(existente));

        autorService.eliminarAutor(1);

        verify(autorRepository).delete(existente);
    }

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException al eliminar un autor inexistente")
    void debeFallarEliminarSiNoExiste() {
        when(autorRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autorService.eliminarAutor(5))
                .isInstanceOf(EntityNotFoundException.class);

        verify(autorRepository, never()).delete(any());
    }

    // ======================================================
    //                          LIST
    // ======================================================

    @Test
    @DisplayName("Debe listar todos los autores ordenados por nombre")
    void debeListarAutores() {
        List<Autor> autoresMock = List.of(
                new Autor() {{ setNombre("B Autor"); }},
                new Autor() {{ setNombre("A Autor"); }}
        );

        when(autorRepository.findAll(Sort.by("nombre"))).thenReturn(autoresMock);

        List<AutorDTO> autores = autorService.listarAutores();

        assertThat(autores).hasSize(2);
        verify(autorRepository).findAll(Sort.by("nombre"));
    }

    // ======================================================
    //                         SEARCH
    // ======================================================

    @Test
    @DisplayName("Debe lanzar excepción si el nombre a buscar es null o vacío")
    void debeFallarBuscarNombreInvalido() {

        assertThatThrownBy(() -> autorService.buscarAutoresNombre(null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> autorService.buscarAutoresNombre("  "))
                .isInstanceOf(IllegalArgumentException.class);

        verify(autorRepository, never()).findByNombreContainingIgnoreCaseOrderByNombre(any());
    }

    @Test
    @DisplayName("Debe buscar autores por nombre")
    void debeBuscarAutoresPorNombre() {
        Autor a1 = new Autor(); a1.setNombre("Mario Vargas Llosa");
        Autor a2 = new Autor(); a2.setNombre("Mario Benedetti");

        when(autorRepository.findByNombreContainingIgnoreCaseOrderByNombre("Mario"))
                .thenReturn(List.of(a1, a2));

        List<Autor> resultados = autorService.buscarAutoresNombre("Mario");

        assertThat(resultados).hasSize(2);
        verify(autorRepository).findByNombreContainingIgnoreCaseOrderByNombre("Mario");
    }
}