package gm.tienda.libros.repository;

import gm.tienda.libros.model.Cliente;
import gm.tienda.libros.model.Venta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class VentaRepositoryTest {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // ---------- SAVE ----------
    @Test
    @DisplayName("Debe guardar y recuperar una venta correctamente")
    void debeGuardarYRecuperarVenta() {
        Cliente cliente = new Cliente("Juan Pérez", "juan@test.com", "099123456");
        clienteRepository.save(cliente);

        Venta venta = new Venta();
        venta.setCodigo("V001");
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(new BigDecimal("150.75"));
        venta.setCodMoneda("USD");
        venta.setIdCliente(cliente.getId());

        ventaRepository.saveAndFlush(venta);

        // Recuperamos la venta y forzamos carga del cliente
        Optional<Venta> resultado = ventaRepository.findByCodigo("V001");

        assertThat(resultado).isPresent();

        Venta ventaRecuperada = resultado.get();
        assertThat(ventaRecuperada.getTotal()).isEqualByComparingTo("150.75");

        // Forzar inicialización del cliente
        Cliente clienteAsociado = clienteRepository.findById(ventaRecuperada.getIdCliente()).orElseThrow();
        assertThat(clienteAsociado.getEmail()).isEqualTo("juan@test.com");
    }

    // ---------- UNIQUE CONSTRAINT ----------
    @Test
    @DisplayName("Debe fallar si el código de venta está duplicado")
    void debeFallarSiCodigoDuplicado() {
        Cliente cliente = clienteRepository.save(new Cliente("Ana", "ana@test.com", "099888888"));

        Venta v1 = new Venta("V001", LocalDateTime.now(), new BigDecimal("100.00"), "USD", cliente.getId(), null);
        Venta v2 = new Venta("V001", LocalDateTime.now(), new BigDecimal("200.00"), "USD", cliente.getId(), null);

        ventaRepository.save(v1);

        assertThatThrownBy(() -> ventaRepository.saveAndFlush(v2))
                .isInstanceOf(Exception.class);
    }

    // ---------- UPDATE ----------
    @Test
    @DisplayName("Debe actualizar el total de una venta existente")
    void debeActualizarVentaExistente() {
        Cliente cliente = clienteRepository.save(new Cliente("Pedro", "pedro@test.com", "099777777"));

        Venta venta = new Venta("V002", LocalDateTime.now(), new BigDecimal("300.00"), "USD", cliente.getId(), null);
        Venta guardada = ventaRepository.save(venta);

        guardada.setTotal(new BigDecimal("350.00"));
        Venta actualizada = ventaRepository.save(guardada);

        assertThat(actualizada.getTotal()).isEqualByComparingTo("350.00");
    }

    // ---------- DELETE ----------
    @Test
    @DisplayName("Debe eliminar una venta existente")
    void debeEliminarVenta() {
        Cliente cliente = clienteRepository.save(new Cliente("Marta", "marta@test.com", "099666666"));

        Venta venta = new Venta("V003", LocalDateTime.now(), new BigDecimal("180.00"), "USD", cliente.getId(), null);
        Venta guardada = ventaRepository.save(venta);

        ventaRepository.delete(guardada);

        Optional<Venta> resultado = ventaRepository.findById(guardada.getId());
        assertThat(resultado).isEmpty();
    }

    // ---------- FIND BY ID ----------
    @Test
    @DisplayName("findById debe retornar venta si existe")
    void findById_debeRetornarVentaSiExiste() {
        Cliente cliente = clienteRepository.save(new Cliente("Laura", "laura@test.com", "099555555"));

        Venta venta = new Venta("V004", LocalDateTime.now(), new BigDecimal("250.00"), "USD", cliente.getId(), null);
        Venta guardada = ventaRepository.save(venta);

        Optional<Venta> resultado = ventaRepository.findById(guardada.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCodigo()).isEqualTo("V004");
    }

    @Test
    @DisplayName("findById debe retornar empty si no existe")
    void findById_debeRetornarEmptySiNoExiste() {
        Optional<Venta> resultado = ventaRepository.findById(999);
        assertThat(resultado).isEmpty();
    }
}
