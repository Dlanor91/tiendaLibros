package gm.tienda_libros.RepositoryTest;

import gm.tienda_libros.model.GeneroLiterario;
import gm.tienda_libros.repository.GeneroLiterarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
public class GeneroLiterarioRepositoryTest {

    @Autowired
    private GeneroLiterarioRepository repository;

    @PersistenceContext
    private EntityManager em;

    private GeneroLiterario genero1;

    @BeforeEach
    void setup() {
        genero1 = new GeneroLiterario();
        genero1.setNombre("Ficción");
        genero1.setCodigo("F01");
    }

    @Test
    @DisplayName("Guardar genero: debe persistir y asignar id")
    @Rollback
    void guardarGeneroPersisteYAsignaId() {
        GeneroLiterario saved = repository.save(genero1);
        assertThat(saved.getId()).isNotNull();
        Optional<GeneroLiterario> fetched = repository.findById(saved.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getNombre()).isEqualTo("Ficción");
    }

    @Test
    @DisplayName("Buscar por codigo: debe devolver optional con el genero")
    void buscarPorCodigoDevuelveGenero() {
        repository.save(genero1);
        Optional<GeneroLiterario> byCodigo = repository.findByCodigo("F01");
        assertThat(byCodigo).isPresent();
        assertThat(byCodigo.get().getNombre()).isEqualTo("Ficción");
    }

    @Test
    @DisplayName("Duplicado de codigo o nombre: debe lanzar excepción por unique constraint")
    @Rollback
    void duplicadoCodigoONombreLanzaException() {
        repository.save(genero1);
        GeneroLiterario dup = new GeneroLiterario();
        dup.setNombre("Ficción");
        dup.setCodigo("F01");

        // depending on DB and JPA provider this puede lanzarse al flush o commit
        assertThatThrownBy(() -> {
            repository.saveAndFlush(dup);
            em.flush();
        }).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Listar generos: debe devolver lista con elementos guardados")
    void listarGenerosDevuelveLista() {
        repository.save(new GeneroLiterario( "Historia", "HIS"));
        repository.save(new GeneroLiterario( "Poesía", "POE"));
        List<GeneroLiterario> all = repository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Si no existe por codigo, el optional debe estar vacío")
    void buscarPorCodigoNoExisteDevuelveEmpty() {
        Optional<GeneroLiterario> byCodigo = repository.findByCodigo("NOEX");
        assertThat(byCodigo).isEmpty();
    }
}
