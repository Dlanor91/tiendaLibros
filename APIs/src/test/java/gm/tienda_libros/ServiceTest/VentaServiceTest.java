package gm.tienda_libros.ServiceTest;

import gm.tienda_libros.DTOs.VentaDTO;
import gm.tienda_libros.model.Cliente;
import gm.tienda_libros.model.Venta;
import gm.tienda_libros.repository.VentaRepository;
import gm.tienda_libros.service.imp.VentaService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private VentaService ventaService;

    // ---------- CREATE ----------
    @Test
    @DisplayName("Debe insertar una venta válida con cliente existente")
    void debeInsertarVentaValida() {
        Venta venta = new Venta();
        venta.setCodigo("V001");
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(new BigDecimal("250.50"));
        venta.setCodMoneda("USD");
        venta.setIdCliente(1); // cumple las validaciones

        when(ventaRepository.save(venta)).thenReturn(venta);

        Venta resultado = ventaService.crearVenta(venta);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCodigo()).isEqualTo("V001");
        verify(ventaRepository).save(venta);
    }

    @Test
    @DisplayName("Debe lanzar excepción al insertar venta con idCliente null o inválido")
    void debeLanzarExcepcionSiIdClienteEsInvalido() {
        Venta venta = new Venta();
        venta.setCodigo("V002");
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(new BigDecimal("100.00"));
        venta.setCodMoneda("USD");
        venta.setIdCliente(0); // inválido por @Min(1)

        assertThatThrownBy(() -> ventaService.crearVenta(venta))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cliente no puede ser null o menor que 1");

        verify(ventaRepository, never()).save(any());
    }

    // ---------- READ ----------
    @Test
    @DisplayName("Debe retornar venta por ID si existe")
    void debeListarVentaPorIdExistente() {
        Venta venta = new Venta();
        venta.setId(1);
        venta.setCodigo("V010");
        venta.setTotal(new BigDecimal("500"));
        venta.setIdCliente(2);

        Cliente cliente = new Cliente();
        cliente.setId(2);
        cliente.setNombre("Juan Cliente");
        venta.setCliente(cliente); // mapeo de lectura

        when(ventaRepository.findById(1)).thenReturn(Optional.of(venta));

        Venta resultado = ventaService.obtenerVentaById(1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCodigo()).isEqualTo("V010");
        assertThat(resultado.getCliente().getNombre()).isEqualTo("Juan Cliente");
    }

    @Test
    @DisplayName("Debe lanzar excepción si venta no existe al buscar por ID")
    void debeLanzarExcepcionSiVentaNoExiste() {
        when(ventaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ventaService.obtenerVentaById(99))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("no encontrada");
    }

    // ---------- UPDATE ----------
    @Test
    @DisplayName("Debe actualizar una venta existente")
    void debeActualizarVentaExistente() {
        Venta existente = new Venta();
        existente.setId(1);
        existente.setCodigo("V100");
        existente.setTotal(new BigDecimal("300"));
        existente.setIdCliente(1);

        when(ventaRepository.findById(1)).thenReturn(Optional.of(existente));
        when(ventaRepository.save(any(Venta.class))).thenReturn(existente);

        Venta cambios = new Venta();
        cambios.setTotal(new BigDecimal("450.99"));
        cambios.setCodMoneda("USD");

        Venta actualizado = ventaService.actualizarVenta(1, cambios);

        assertThat(actualizado.getTotal()).isEqualByComparingTo("450.99");
        verify(ventaRepository).save(any(Venta.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar venta inexistente")
    void debeLanzarExcepcionAlActualizarInexistente() {
        when(ventaRepository.findById(10)).thenReturn(Optional.empty());

        Venta cambios = new Venta();
        cambios.setTotal(new BigDecimal("150"));

        assertThatThrownBy(() -> ventaService.actualizarVenta(10, cambios))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("no encontrada");
    }

    // ---------- DELETE ----------
    @Test
    @DisplayName("Debe eliminar venta existente")
    void debeEliminarVentaExistente() {
        Venta existente = new Venta();
        existente.setId(5);

        when(ventaRepository.findById(5)).thenReturn(Optional.of(existente));

        ventaService.eliminarVenta(5);

        verify(ventaRepository).delete(existente);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar venta inexistente")
    void debeLanzarExcepcionAlEliminarInexistente() {
        when(ventaRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ventaService.eliminarVenta(999))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("no encontrada");
    }

    // ---------- LIST ----------
    @Test
    @DisplayName("Debe listar todas las ventas con sus clientes")
    void debeListarTodasLasVentasConClientes() {
        Cliente c1 = new Cliente();
        c1.setId(1);
        c1.setNombre("Juan");

        Cliente c2 = new Cliente();
        c2.setId(2);
        c2.setNombre("María");

        Venta v1 = new Venta();
        v1.setId(1);
        v1.setCodigo("A01");
        v1.setIdCliente(1);
        v1.setCliente(c1);

        Venta v2 = new Venta();
        v2.setId(2);
        v2.setCodigo("A02");
        v2.setIdCliente(2);
        v2.setCliente(c2);

        when(ventaRepository.findAll(any(Sort.class))).thenReturn(List.of(v1, v2));

        List<VentaDTO> ventas = ventaService.listarVentas();

        assertThat(ventas).hasSize(2);
        assertThat(ventas.get(0).nombreCliente()).isEqualTo("Juan");
        assertThat(ventas.get(1).nombreCliente()).isEqualTo("María");
    }
}
