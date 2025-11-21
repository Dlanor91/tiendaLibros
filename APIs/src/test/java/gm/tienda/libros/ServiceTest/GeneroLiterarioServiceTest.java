package gm.tienda.libros.ServiceTest;

import gm.tienda.libros.model.GeneroLiterario;
import gm.tienda.libros.repository.GeneroLiterarioRepository;
import gm.tienda.libros.service.imp.GeneroLiterarioService;
import jakarta.persistence.EntityExistsException;
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
class GeneroLiterarioServiceTest {

    @Mock
    private GeneroLiterarioRepository generoRepository;

    @InjectMocks
    private GeneroLiterarioService generoService;

    // ===============================================================
    //                          CREATE
    // ===============================================================
    @Test
    @DisplayName("Debe crear un género literario si el código no existe")
    void debeCrearGeneroSiCodigoNoExiste() {
        when(generoRepository.findByCodigo("F01")).thenReturn(Optional.empty());

        GeneroLiterario nuevo = new GeneroLiterario();
        nuevo.setNombre("Ficción");
        nuevo.setCodigo("F01");

        generoService.agregarGeneroLiterario(nuevo);

        verify(generoRepository).save(nuevo);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el código del género ya existe")
    void debeLanzarExcepcionSiCodigoYaExiste() {
        GeneroLiterario existente = new GeneroLiterario();
        existente.setCodigo("HIS");
        when(generoRepository.findByCodigo("HIS")).thenReturn(Optional.of(existente));

        GeneroLiterario nuevo = new GeneroLiterario();
        nuevo.setCodigo("HIS");

        assertThatThrownBy(() -> generoService.agregarGeneroLiterario(nuevo))
                .isInstanceOf(EntityExistsException.class)
                .hasMessageContaining("Ya existe");
    }

    // ===============================================================
    //                          FIND BY ID
    // ===============================================================
    @Test
    @DisplayName("Debe obtener un género literario por su código")
    void debeObtenerGeneroPorCodigo() {
        GeneroLiterario genero = new GeneroLiterario();
        genero.setCodigo("POE");
        genero.setNombre("Poesía");
        when(generoRepository.findByCodigo("POE")).thenReturn(Optional.of(genero));

        GeneroLiterario resultado = generoService.obtenerGeneroLiterarioByCodigo("POE");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Poesía");
    }

    @Test
    @DisplayName("Debe lanzar excepción si no existe un género con el código indicado")
    void debeLanzarExcepcionSiGeneroNoExiste() {
        when(generoRepository.findByCodigo("XYZ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generoService.obtenerGeneroLiterarioByCodigo("XYZ"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no encontrado");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el código está vacío al buscar género")
    void debeLanzarExcepcionPorCodigoVacioEnFind() {

        assertThatThrownBy(() -> generoService.obtenerGeneroLiterarioByCodigo("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("vacío");
    }

    // ===============================================================
    //                          UPDATE
    // ===============================================================
    @Test
    @DisplayName("Debe actualizar un género existente correctamente")
    void debeActualizarGeneroExistente() {
        GeneroLiterario existente = new GeneroLiterario();
        existente.setCodigo("FIC");
        existente.setNombre("Ficción");

        when(generoRepository.findByCodigo("FIC")).thenReturn(Optional.of(existente));
        when(generoRepository.save(any(GeneroLiterario.class))).thenReturn(existente);

        GeneroLiterario cambios = new GeneroLiterario();
        cambios.setNombre("Ficción Moderna");
        cambios.setCodigo("FIC");

        GeneroLiterario actualizado = generoService.actualizarGeneroLiterario("FIC", cambios);

        assertThat(actualizado.getNombre()).isEqualTo("Ficción Moderna");
        verify(generoRepository).save(existente);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar actualizar un género inexistente")
    void debeLanzarExcepcionAlActualizarGeneroInexistente() {
        when(generoRepository.findByCodigo("ZZZ")).thenReturn(Optional.empty());

        GeneroLiterario cambios = new GeneroLiterario();
        cambios.setNombre("Nuevo");

        assertThatThrownBy(() -> generoService.actualizarGeneroLiterario("ZZZ", cambios))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No encontrado");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el código está vacío al actualizar género")
    void debeLanzarExcepcionPorCodigoVacioEnUpdate() {

        GeneroLiterario cambios = new GeneroLiterario();
        cambios.setNombre("Nuevo nombre");

        assertThatThrownBy(() -> generoService.actualizarGeneroLiterario("   ", cambios))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("vacío");
    }

    // ===============================================================
    //                          DELETE
    // ===============================================================
    @Test
    @DisplayName("Debe eliminar un género existente correctamente")
    void debeEliminarGeneroExistente() {
        GeneroLiterario existente = new GeneroLiterario();
        existente.setCodigo("HIS");

        when(generoRepository.findByCodigo("HIS")).thenReturn(Optional.of(existente));

        generoService.eliminarGeneroLiterario("HIS");

        verify(generoRepository).delete(existente);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar eliminar un género inexistente")
    void debeLanzarExcepcionAlEliminarGeneroInexistente() {
        when(generoRepository.findByCodigo("ZZZ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generoService.eliminarGeneroLiterario("ZZZ"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No encontrado");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el código está vacío al eliminar género")
    void debeLanzarExcepcionPorCodigoVacioEnDelete() {

        assertThatThrownBy(() -> generoService.eliminarGeneroLiterario("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("vacío");
    }

    // ===============================================================
    //                          LIST
    // ===============================================================
    @Test
    @DisplayName("Debe listar todos los géneros literarios ordenados por código")
    void debeListarTodosLosGenerosOrdenadosPorCodigo() {
        List<GeneroLiterario> mockGeneros = List.of(
                new GeneroLiterario("Ficción", "FIC"),
                new GeneroLiterario("Poesía", "POE")
        );
        when(generoRepository.findAll(Sort.by("codigo"))).thenReturn(mockGeneros);

        List<GeneroLiterario> generos = generoService.listarGenerosLiterarios();

        assertThat(generos).hasSize(2);
        assertThat(generos.get(0).getCodigo()).isEqualTo("FIC");
        assertThat(generos.get(1).getCodigo()).isEqualTo("POE");

        verify(generoRepository).findAll(Sort.by("codigo"));
        verifyNoMoreInteractions(generoRepository);
    }
}
