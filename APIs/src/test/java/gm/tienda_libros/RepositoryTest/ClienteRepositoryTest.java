package gm.tienda_libros.RepositoryTest;

import gm.tienda_libros.model.Cliente;
import gm.tienda_libros.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    //Save
    @Test
    void debeGuardarYRecuperarCliente() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan Pérez");
        cliente.setEmail("juan@test.com");
        cliente.setTelefono("099123456");

        clienteRepository.save(cliente);

        Optional<Cliente> resultado = clienteRepository.findByEmail("juan@test.com");
        assertThat(resultado).isPresent();
    }

    //FindByEmail
    @Test
    void debeFallarSiEmailDuplicado() {
        Cliente c1 = new Cliente();
        c1.setNombre("Juan");
        c1.setEmail("repetido@test.com");
        c1.setTelefono("111");

        Cliente c2 = new Cliente();
        c2.setNombre("Pedro");
        c2.setEmail("repetido@test.com");
        c2.setTelefono("222");

        clienteRepository.save(c1);
        assertThatThrownBy(() -> clienteRepository.saveAndFlush(c2))
                .isInstanceOf(Exception.class);
    }

    //Update
    @Test
    void debeActualizarClienteExistente() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Luis");
        cliente.setEmail("luis@test.com");
        cliente.setTelefono("099777777");
        Cliente guardado = clienteRepository.save(cliente);

        guardado.setTelefono("099999999");
        Cliente actualizado = clienteRepository.save(guardado);

        assertThat(actualizado.getTelefono()).isEqualTo("099999999");
    }

    //Delete
    @Test
    void debeEliminarCliente() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Marta");
        cliente.setEmail("marta@test.com");
        cliente.setTelefono("099666666");
        Cliente guardado = clienteRepository.save(cliente);

        clienteRepository.delete(guardado);

        Optional<Cliente> resultado = clienteRepository.findById(guardado.getId());
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("findById debe retornar cliente si existe")
    void findById_debeRetornarClienteSiExiste() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Ana");
        cliente.setEmail("ana@test.com");
        cliente.setTelefono("099888888");
        Cliente guardado = clienteRepository.save(cliente);

        Optional<Cliente> resultado = clienteRepository.findById(guardado.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("ana@test.com");
    }

    @Test
    @DisplayName("findById debe retornar empty si no existe")
    void findById_debeRetornarEmptySiNoExiste() {
        Optional<Cliente> resultado = clienteRepository.findById(999);
        assertThat(resultado).isEmpty();
    }
}

