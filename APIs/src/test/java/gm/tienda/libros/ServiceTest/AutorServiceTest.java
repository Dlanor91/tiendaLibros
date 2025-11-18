package gm.tienda.libros.ServiceTest;

import gm.tienda.libros.model.Autor;
import gm.tienda.libros.repository.AutorRepository;
import gm.tienda.libros.service.imp.AutorService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutorServiceTest {

    @Mock
    private AutorRepository autorRepository;

    @InjectMocks
    private AutorService autorService;

    // ---------- CREATE ----------
    @Test
    @DisplayName("Debe crear un autor correctamente")
    void debeCrearAutor() {
        Autor nuevo = new Autor();
        nuevo.setNombre("Gabriel García Márquez");

        when(autorRepository.save(nuevo)).thenReturn(nuevo);

        Autor creado = autorService.crearAutor(nuevo);

        assertThat(creado).isEqualTo(nuevo);
        verify(autorRepository).save(nuevo);
    }

    // ---------- READ ----------
    @Test
    @DisplayName("Debe obtener un autor por su ID")
    void debeObtenerAutorPorId() {
        Autor autor = new Autor();
        autor.setId(1);
        autor.setNombre("Isabel Allende");

        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));

        Autor resultado = autorService.obtenerAutorById(1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Isabel Allende");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el autor no existe")
    void debeLanzarExcepcionSiAutorNoExiste() {
        when(autorRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autorService.obtenerAutorById(99))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ---------- UPDATE ----------
    @Test
    @DisplayName("Debe actualizar un autor existente correctamente")
    void debeActualizarAutorExistente() {
        Autor existente = new Autor();
        existente.setId(1);
        existente.setNombre("Ernest Hemingway");

        when(autorRepository.findById(1)).thenReturn(Optional.of(existente));
        when(autorRepository.save(any(Autor.class))).thenAnswer(i -> i.getArgument(0));

        Autor cambios = new Autor();
        cambios.setNombre("Ernest Hemingway Actualizado");

        Autor actualizado = autorService.actualizarAutor(1, cambios);

        assertThat(actualizado.getNombre()).isEqualTo("Ernest Hemingway Actualizado");
        verify(autorRepository).save(existente);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar un autor inexistente")
    void debeLanzarExcepcionAlActualizarAutorInexistente() {
        when(autorRepository.findById(10)).thenReturn(Optional.empty());

        Autor cambios = new Autor();
        cambios.setNombre("Nuevo Autor");

        assertThatThrownBy(() -> autorService.actualizarAutor(10, cambios))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ---------- DELETE ----------
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
    @DisplayName("Debe lanzar excepción al eliminar un autor inexistente")
    void debeLanzarExcepcionAlEliminarAutorInexistente() {
        when(autorRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autorService.eliminarAutor(5))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ---------- LIST ----------
    @Test
    @DisplayName("Debe listar todos los autores ordenados por nombre")
    void debeListarTodosLosAutores() {
        List<Autor> autoresMock = List.of(
                new Autor() {{ setNombre("B Autor"); }},
                new Autor() {{ setNombre("A Autor"); }}
        );

        // Simular que el repository devuelve la lista ordenada
        when(autorRepository.findAll(Sort.by("nombre"))).thenReturn(autoresMock);

        List<Autor> autores = autorService.listarAutores();

        assertThat(autores).hasSize(2);
        assertThat(autores.get(0).getNombre()).isEqualTo("B Autor");
        assertThat(autores.get(1).getNombre()).isEqualTo("A Autor");

        verify(autorRepository).findAll(Sort.by("nombre")); // verificar que se llamó con Sort
    }

    // ---------- SEARCH ----------
    @Test
    @DisplayName("Debe buscar autores por nombre")
    void debeBuscarAutoresPorNombre() {
        Autor autor1 = new Autor();
        autor1.setNombre("Mario Vargas Llosa");
        Autor autor2 = new Autor();
        autor2.setNombre("Mario Benedetti");

        when(autorRepository.findByNombreContainingIgnoreCaseOrderByNombre("Mario")).thenReturn(List.of(autor1, autor2));

        List<Autor> resultados = autorService.buscarAutoresNombre("Mario");

        assertThat(resultados).hasSize(2);
        assertThat(resultados.get(0).getNombre()).contains("Mario");
        verify(autorRepository).findByNombreContainingIgnoreCaseOrderByNombre("Mario");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el nombre es null o vacío al buscar")
    void debeLanzarExcepcionSiNombreInvalido() {
        assertThatThrownBy(() -> autorService.buscarAutoresNombre(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede ser null");

        assertThatThrownBy(() -> autorService.buscarAutoresNombre("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede ser null");
    }
}
