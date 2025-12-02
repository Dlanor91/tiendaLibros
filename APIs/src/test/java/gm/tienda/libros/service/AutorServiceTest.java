package gm.tienda.libros.service;

import gm.tienda.libros.dto.AutorDTO;
import gm.tienda.libros.dto.AutorDetalleDTO;
import gm.tienda.libros.dto.AutorRequestDTO;
import gm.tienda.libros.model.Autor;
import gm.tienda.libros.model.GeneroLiterario;
import gm.tienda.libros.model.Libro;
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

    // ======================================================
    //                        CREATE
    // ======================================================

    @Test
    @DisplayName("Debe crear un autor correctamente")
    void debeCrearAutor() {
        AutorRequestDTO dto = new AutorRequestDTO(1,"Gabriel", "García Márquez");

        Autor guardado = new Autor();
        guardado.setId(1);
        guardado.setNombre("Gabriel");
        guardado.setApellidos("García Márquez");

        when(autorRepository.save(any(Autor.class))).thenReturn(guardado);

        Autor creado = autorService.crearAutor(dto);

        assertThat(creado).isNotNull();
        assertThat(creado.getNombre()).isEqualTo("Gabriel");
        assertThat(creado.getApellidos()).isEqualTo("García Márquez");

        verify(autorRepository).save(any(Autor.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el DTO a crear es null")
    void debeLanzarExcepcionCrearNull() {
        assertThatThrownBy(() -> autorService.crearAutor(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("no puede ser null");

        verifyNoInteractions(autorRepository);
    }

    // ======================================================
    //                        FIND BY ID
    // ======================================================

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el ID es inválido")
    void debeLanzarExcepcionIdInvalido() {
        assertThatThrownBy(() -> autorService.obtenerAutorPorId(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mayor que 0");

        verifyNoInteractions(autorRepository);
    }

    @Test
    @DisplayName("Debe obtener un autor existente con sus libros")
    void debeObtenerAutorPorId() {
        GeneroLiterario genero = new GeneroLiterario();
        genero.setNombre("Realismo Mágico");

        Libro libro = new Libro();
        libro.setIsbn("123");
        libro.setNombre("Cien años de soledad");
        libro.setGeneroLiterario(genero);

        Autor autor = new Autor();
        autor.setId(1);
        autor.setNombre("Gabriel");
        autor.setApellidos("García Márquez");
        autor.setLibros(List.of(libro));

        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));

        AutorDetalleDTO dto = autorService.obtenerAutorPorId(1);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.libros()).hasSize(1);
        assertThat(dto.libros().get(0).nombre()).isEqualTo("Cien años de soledad");

        verify(autorRepository).findById(1);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el autor no existe")
    void debeLanzarExcepcionSiNoExiste() {
        when(autorRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autorService.obtenerAutorPorId(99))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No se encontró");

        verify(autorRepository).findById(99);
    }

    @Test
    @DisplayName("Debe mapear libro con género null sin fallar")
    void debeMapearLibroSinGenero() {
        Libro libro = new Libro();
        libro.setNombre("Libro sin género");
        libro.setGeneroLiterario(null);

        Autor autor = new Autor();
        autor.setId(1);
        autor.setNombre("Autor");
        autor.setApellidos("X");
        autor.setLibros(List.of(libro));

        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));

        AutorDetalleDTO dto = autorService.obtenerAutorPorId(1);

        assertThat(dto.libros().get(0).nombreGeneroLiterario()).isNull();
    }

    // ======================================================
    //                        UPDATE
    // ======================================================

    @Test
    @DisplayName("Debe actualizar autor existente")
    void debeActualizarAutor() {
        Autor existente = new Autor();
        existente.setId(1);
        existente.setNombre("Viejo");
        existente.setApellidos("Apellido");

        AutorRequestDTO cambios = new AutorRequestDTO(1,"Nuevo","Actualizado");

        when(autorRepository.findById(1)).thenReturn(Optional.of(existente));
        when(autorRepository.save(existente)).thenReturn(existente);

        Autor actualizado = autorService.actualizarAutor(1, cambios);

        assertThat(actualizado.getNombre()).isEqualTo("Nuevo");
        assertThat(actualizado.getApellidos()).isEqualTo("Actualizado");

        verify(autorRepository).save(existente);
    }

    @Test
    @DisplayName("Debe fallar al actualizar un autor inexistente")
    void debeFallarActualizarInexistente() {
        when(autorRepository.findById(10)).thenReturn(Optional.empty());

        AutorRequestDTO dto = new AutorRequestDTO(10,"X","Y");

        assertThatThrownBy(() -> autorService.actualizarAutor(10, dto))
                .isInstanceOf(EntityNotFoundException.class);

        verify(autorRepository).findById(10);
        verifyNoMoreInteractions(autorRepository);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el DTO es null en update")
    void debeLanzarExcepcionUpdateNull() {
        assertThatThrownBy(() -> autorService.actualizarAutor(1, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("no puede ser null");

        verifyNoInteractions(autorRepository);
    }

    // ======================================================
    //                        DELETE
    // ======================================================

    @Test
    @DisplayName("Debe fallar al eliminar si el ID es inválido")
    void debeFallarEliminarIdInvalido() {
        assertThatThrownBy(() -> autorService.eliminarAutor(0))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(autorRepository);
    }

    @Test
    @DisplayName("Debe eliminar un autor existente")
    void debeEliminarAutor() {
        Autor existente = new Autor();
        existente.setId(1);

        when(autorRepository.findById(1)).thenReturn(Optional.of(existente));

        autorService.eliminarAutor(1);

        verify(autorRepository).delete(existente);
    }

    @Test
    @DisplayName("Debe fallar al eliminar un autor inexistente")
    void debeFallarEliminarInexistente() {
        when(autorRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autorService.eliminarAutor(5))
                .isInstanceOf(EntityNotFoundException.class);

        verify(autorRepository).findById(5);
        verifyNoMoreInteractions(autorRepository);
    }

    // ======================================================
    //                        LIST
    // ======================================================

    @Test
    @DisplayName("Debe listar autores ordenados por nombre")
    void debeListarAutores() {
        Autor a1 = new Autor(); a1.setNombre("B");
        Autor a2 = new Autor(); a2.setNombre("A");

        when(autorRepository.findAll(Sort.by("nombre"))).thenReturn(List.of(a1,a2));

        List<AutorDTO> lista = autorService.listarAutores();

        assertThat(lista).hasSize(2);
        verify(autorRepository).findAll(Sort.by("nombre"));
    }

    // ======================================================
    //                        SEARCH
    // ======================================================

    @Test
    @DisplayName("Debe fallar al buscar nombre null")
    void debeFallarBuscarNull() {
        assertThatThrownBy(() -> autorService.buscarAutoresNombre(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("no puede ser null");

        verifyNoInteractions(autorRepository);
    }

    @Test
    @DisplayName("Debe buscar autores por nombre correctamente")
    void debeBuscarAutores() {
        Autor a1 = new Autor(); a1.setNombre("Mario A");
        Autor a2 = new Autor(); a2.setNombre("Mario B");

        when(autorRepository.findByNombreContainingIgnoreCaseOrderByNombre("Mario"))
                .thenReturn(List.of(a1, a2));

        List<Autor> resultado = autorService.buscarAutoresNombre("Mario");

        assertThat(resultado).hasSize(2);
        verify(autorRepository).findByNombreContainingIgnoreCaseOrderByNombre("Mario");
    }
}