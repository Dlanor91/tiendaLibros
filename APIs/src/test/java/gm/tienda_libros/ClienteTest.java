package gm.tienda_libros;

import gm.tienda_libros.model.Cliente;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClienteTest {
    @Test
    void debeCrearClienteConDatosBasicos() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan Pérez");
        cliente.setEmail("juan@test.com");
        cliente.setTelefono("099123456");

        assertThat(cliente.getNombre()).isEqualTo("Juan Pérez");
        assertThat(cliente.getEmail()).isEqualTo("juan@test.com");
        assertThat(cliente.getTelefono()).isEqualTo("099123456");
    }
}
