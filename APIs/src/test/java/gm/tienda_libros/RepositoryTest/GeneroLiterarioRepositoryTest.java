package gm.tienda_libros.RepositoryTest;

import gm.tienda_libros.model.GeneroLiterario;
import gm.tienda_libros.repository.GeneroLiterarioRepository;
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
class GeneroLiterarioRepositoryTest {

    @Autowired
    private GeneroLiterarioRepository generoRepository;

    // ---------- SAVE ----------
    @Test
    void debeGuardarYRecuperarGenero() {
        GeneroLiterario genero = new GeneroLiterario();
        genero.setNombre("Ficción");
        genero.setCodigo("F01");

        generoRepository.save(genero);

        Optional<GeneroLiterario> resultado = generoRepository.findByCodigo("F01");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Ficción");
    }

    // ---------- DUPLICADO ----------
    @Test
    void debeFallarSiCodigoDuplicado() {
        GeneroLiterario g1 = new GeneroLiterario();
        g1.setNombre("Historia");
        g1.setCodigo("H01");

        GeneroLiterario g2 = new GeneroLiterario();
        g2.setNombre("Poesía");
        g2.setCodigo("H01");

        generoRepository.save(g1);

        assertThatThrownBy(() -> generoRepository.saveAndFlush(g2))
                .isInstanceOf(Exception.class);
    }

    // ---------- UPDATE ----------
    @Test
    void debeActualizarGeneroExistente() {
        GeneroLiterario genero = new GeneroLiterario();
        genero.setNombre("Drama");
        genero.setCodigo("D01");
        GeneroLiterario guardado = generoRepository.save(genero);

        guardado.setNombre("Drama Contemporáneo");
        GeneroLiterario actualizado = generoRepository.save(guardado);

        assertThat(actualizado.getNombre()).isEqualTo("Drama Contemporáneo");
    }

    // ---------- DELETE ----------
    @Test
    void debeEliminarGenero() {
        GeneroLiterario genero = new GeneroLiterario();
        genero.setNombre("Romance");
        genero.setCodigo("R01");
        GeneroLiterario guardado = generoRepository.save(genero);

        generoRepository.delete(guardado);

        Optional<GeneroLiterario> resultado = generoRepository.findById(guardado.getId());
        assertThat(resultado).isEmpty();
    }

    // ---------- FIND BY CODIGO ----------
    @Test
    @DisplayName("findByCodigo debe retornar género si existe")
    void findByCodigo_debeRetornarGeneroSiExiste() {
        GeneroLiterario genero = new GeneroLiterario();
        genero.setNombre("Aventura");
        genero.setCodigo("A01");
        generoRepository.save(genero);

        Optional<GeneroLiterario> resultado = generoRepository.findByCodigo("A01");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Aventura");
    }

    @Test
    @DisplayName("findByCodigo debe retornar empty si no existe")
    void findByCodigo_debeRetornarEmptySiNoExiste() {
        Optional<GeneroLiterario> resultado = generoRepository.findByCodigo("NOX");
        assertThat(resultado).isEmpty();
    }

    // ---------- FIND ALL ----------
    @Test
    @DisplayName("findAll debe devolver lista de géneros guardados")
    void findAll_debeDevolverListaDeGeneros() {
        generoRepository.save(new GeneroLiterario("Fantasía", "FAN"));
        generoRepository.save(new GeneroLiterario("Ciencia Ficción", "CFI"));

        List<GeneroLiterario> lista = generoRepository.findAll();

        assertThat(lista).isNotEmpty();
        assertThat(lista).extracting(GeneroLiterario::getCodigo)
                .contains("FAN", "CFI");
    }
}
