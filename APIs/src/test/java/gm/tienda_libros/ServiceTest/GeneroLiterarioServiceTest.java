package gm.tienda_libros.ServiceTest;

import gm.tienda_libros.model.GeneroLiterario;
import gm.tienda_libros.repository.GeneroLiterarioRepository;
import gm.tienda_libros.service.IGeneroLiterarioService;
import gm.tienda_libros.service.imp.GeneroLiterarioService;
import jakarta.inject.Inject;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GeneroLiterarioServiceTest {

    @Mock
    private GeneroLiterarioRepository repository;

    @InjectMocks
    private GeneroLiterarioService service;

    private GeneroLiterario g1;
    private GeneroLiterario g2;

    @BeforeEach
    void setup() {
        g1 = new GeneroLiterario();
        g1.setId(1);
        g1.setNombre("Ficción");
        g1.setCodigo("F01");

        g2 = new GeneroLiterario();
        g2.setId(2);
        g2.setNombre("Historia");
        g2.setCodigo("HIS");
    }




    @Test
    @DisplayName("Obtener por codigo: si existe devuelve el genero")
    void obtenerPorCodigoDevuelveGenero() {
        when(repository.findByCodigo("F01")).thenReturn(Optional.of(g1));

        GeneroLiterario found = service.obtenerGeneroLiterarioByCodigo("F01");

        assertThat(found).isNotNull();
        assertThat(found.getNombre()).isEqualTo("Ficción");
        verify(repository).findByCodigo("F01");
    }

    @Test
    @DisplayName("Listar generos: debe devolver lista desde repo ordenada por codigo")
    void listarGenerosDevuelveListaOrdenada() {
        // Mock del repo con sort
        when(repository.findAll(Sort.by("codigo")))
                .thenReturn(Arrays.asList(g1, g2));

        List<GeneroLiterario> lista = service.listarGenerosLiterarios();

        assertThat(lista).hasSize(2);
        // Verificamos que el orden por codigo se mantiene
        assertThat(lista.get(0).getCodigo()).isEqualTo("F01");
        assertThat(lista.get(1).getCodigo()).isEqualTo("HIS");

        // Verificamos que el repo fue llamado con el sort correcto
        verify(repository, times(1)).findAll(Sort.by("codigo"));
    }

    @Test
    @DisplayName("Agregar genero: caso exitoso devuelve y guarda")
    void agregarGeneroExitoso() {
        when(repository.findByCodigo("NEW")).thenReturn(Optional.empty());
        when(repository.save(any(GeneroLiterario.class))).thenAnswer(inv -> {
            GeneroLiterario arg = inv.getArgument(0);
            arg.setId(99);
            return arg;
        });

        GeneroLiterario nuevo = new GeneroLiterario();
        nuevo.setNombre("Nuevo");
        nuevo.setCodigo("NEW");

        GeneroLiterario saved = service.agregarGeneroLiterario(nuevo);

        assertThat(saved.getId()).isEqualTo(99);
        assertThat(saved.getCodigo()).isEqualTo("NEW");
        verify(repository).findByCodigo("NEW");
        verify(repository).save(any(GeneroLiterario.class));
    }

    @Test
    @DisplayName("Eliminar genero: si no existe lanza EntityNotFoundException")
    void eliminarNoExisteLanza() {
        when(repository.findByCodigo("NOEX")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminarGeneroLiterario("NOEX"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No existe el género");

        verify(repository).findByCodigo("NOEX");
        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("Eliminar genero: caso exitoso invoca delete")
    void eliminarExitosoInvocaDelete() {
        when(repository.findByCodigo("F01")).thenReturn(Optional.of(g1));

        service.eliminarGeneroLiterario("F01");

        verify(repository).findByCodigo("F01");
        verify(repository).delete(g1);
    }

    /* --------- Implementación simple y localizada del servicio para testear con @InjectMocks ---------
       Como en el enunciado sólo diste la interfaz IGeneroLiterarioService, incluyo una implementación mínima
       dentro del test para poder usar @InjectMocks y verificar comportamiento con Mockito. En tu proyecto
       sustituye esto por la implementación real si ya existe.
    */
    static abstract class GeneroLiterarioServiceImpl implements IGeneroLiterarioService {

        private final GeneroLiterarioRepository repo;

        @Inject
        public GeneroLiterarioServiceImpl(GeneroLiterarioRepository repo) {
            this.repo = repo;
        }

        @Override
        public List<GeneroLiterario> listarGenerosLiterarios() {
            return repo.findAll();
        }

        // nota: firma adaptada para test: uso String codigo como parámetro
        public GeneroLiterario obtenerGeneroLiterarioByCodigo(String codigo) {
            return repo.findByCodigo(codigo).orElseThrow(() ->
                    new EntityNotFoundException("Género con código " + codigo + " no encontrado"));
        }

        public GeneroLiterario agregarGeneroLiterario(GeneroLiterario genero) {
            repo.findByCodigo(genero.getCodigo()).ifPresent(g -> {
                throw new EntityExistsException("Género con código " + genero.getCodigo() + " ya existe");
            });
            return repo.save(genero);
        }

        @Override
        public GeneroLiterario actualizarGeneroLiterario(String codigo, GeneroLiterario generoLiterario) {
            // implementación simple para tests - no usada en los tests actuales
            return repo.save(generoLiterario);
        }

        public void eliminarGeneroLiterario(String codigo) {
            GeneroLiterario g = repo.findByCodigo(codigo).orElseThrow(() ->
                    new EntityNotFoundException("Género con código " + codigo + " no encontrado"));
            repo.delete(g);
        }
    }

    @Test
    @DisplayName("Actualizar genero: caso exitoso guarda cambios")
    void actualizarGeneroExitoso() {
        when(repository.findByCodigo("F01")).thenReturn(Optional.of(g1));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        g1.setNombre("Ficción Moderna");
        GeneroLiterario updated = service.actualizarGeneroLiterario("F01", g1);

        assertThat(updated.getNombre()).isEqualTo("Ficción Moderna");
        verify(repository).save(g1);
    }

    @Test
    @DisplayName("Obtener por codigo: si no existe lanza EntityNotFoundException")
    void obtenerPorCodigoNoExisteLanza() {
        when(repository.findByCodigo("XXX")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerGeneroLiterarioByCodigo("XXX"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("no encontrado");

        verify(repository).findByCodigo("XXX");
    }


}
