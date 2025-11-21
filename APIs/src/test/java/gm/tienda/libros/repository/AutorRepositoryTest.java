package gm.tienda.libros.repository;

import gm.tienda.libros.model.Autor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class AutorRepositoryTest {

    @Autowired
    private AutorRepository autorRepository;

    // ---------- SAVE ----------
    @Test
    @DisplayName("Debe guardar un autor y poder recuperarlo por nombre")
    void debeGuardarYRecuperarAutor() {
        Autor autor = new Autor();
        autor.setNombre("Gabriel");
        autor.setApellidos("García Márquez");

        autorRepository.save(autor);

        List<Autor> resultado = autorRepository.findByNombreContainingIgnoreCaseOrderByNombre("Gabriel");
        assertThat(resultado).isNotEmpty();
        assertThat(resultado.get(0).getApellidos()).isEqualTo("García Márquez");
    }

    @Test
    @DisplayName("Debe fallar al intentar guardar un autor sin nombre (violación de @NotBlank)")
    void debeFallarSiNombreEsNuloOVacio() {
        Autor autor = new Autor();
        autor.setNombre(null);
        autor.setApellidos("SinNombre");

        assertThatThrownBy(() -> autorRepository.saveAndFlush(autor))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Debe fallar al intentar guardar un autor sin apellidos (violación de @NotBlank)")
    void debeFallarSiApellidosEsNuloOVacio() {
        Autor autor = new Autor();
        autor.setNombre("Nombre");
        autor.setApellidos("");

        assertThatThrownBy(() -> autorRepository.saveAndFlush(autor))
                .isInstanceOf(Exception.class);
    }

    // ---------- FIND BY NOMBRE ----------
    @Test
    @DisplayName("Debe recuperar todos los autores con el mismo nombre")
    void debeRecuperarTodosLosAutoresPorNombre() {
        Autor a1 = new Autor();
        a1.setNombre("Duplicado");
        a1.setApellidos("Uno");

        Autor a2 = new Autor();
        a2.setNombre("Duplicado");
        a2.setApellidos("Dos");

        autorRepository.save(a1);
        autorRepository.save(a2);

        List<Autor> resultado = autorRepository.findByNombreContainingIgnoreCaseOrderByNombre("Duplicado");

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Autor::getApellidos)
                .containsExactlyInAnyOrder("Uno", "Dos");
    }

    // ---------- UPDATE ----------
    @Test
    @DisplayName("Debe actualizar los apellidos de un autor existente")
    void debeActualizarAutorExistente() {
        Autor autor = new Autor();
        autor.setNombre("Isabel");
        autor.setApellidos("Allende");
        Autor guardado = autorRepository.save(autor);

        guardado.setApellidos("Allende Llona");
        Autor actualizado = autorRepository.save(guardado);

        assertThat(actualizado.getApellidos()).isEqualTo("Allende Llona");
    }

    // ---------- DELETE ----------
    @Test
    @DisplayName("Debe eliminar un autor correctamente")
    void debeEliminarAutor() {
        Autor autor = new Autor();
        autor.setNombre("Jorge");
        autor.setApellidos("Luis Borges");
        Autor guardado = autorRepository.save(autor);

        autorRepository.delete(guardado);

        Optional<Autor> resultado = autorRepository.findById(guardado.getId());
        assertThat(resultado).isEmpty();
    }

    // ---------- FIND BY ID ----------
    @Test
    @DisplayName("findById debe retornar autor si existe en la base de datos")
    void findById_debeRetornarAutorSiExiste() {
        Autor autor = new Autor();
        autor.setNombre("Mario");
        autor.setApellidos("Vargas Llosa");
        Autor guardado = autorRepository.save(autor);

        Optional<Autor> resultado = autorRepository.findById(guardado.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Mario");
    }

    @Test
    @DisplayName("findById debe retornar vacío si el autor no existe")
    void findById_debeRetornarEmptySiNoExiste() {
        Optional<Autor> resultado = autorRepository.findById(999);
        assertThat(resultado).isEmpty();
    }
}
