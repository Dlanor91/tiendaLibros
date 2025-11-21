package gm.tienda.libros.service;

import gm.tienda.libros.dto.VentaDTO;
import gm.tienda.libros.model.Cliente;
import gm.tienda.libros.model.Venta;
import gm.tienda.libros.repository.VentaRepository;
import gm.tienda.libros.service.imp.VentaService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
    @ParameterizedTest(name = "{index} => {1}")
    @MethodSource("proveedorVentasInvalidas")
    @DisplayName("Debe lanzar excepción ante ventas inválidas en crearVenta")
    void debeLanzarExcepcionConVentasInvalidas(Venta venta, String descripcion, String mensajeEsperado) {

        assertThatThrownBy(() -> ventaService.crearVenta(venta))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(mensajeEsperado);

        verify(ventaRepository, never()).save(any());
    }

    private static Stream<Arguments> proveedorVentasInvalidas() {

        Venta ventaIdNull = new Venta();
        ventaIdNull.setCodigo("X001");
        ventaIdNull.setFecha(LocalDateTime.now());
        ventaIdNull.setTotal(new BigDecimal("100"));
        ventaIdNull.setCodMoneda("USD");
        ventaIdNull.setIdCliente(null);

        Venta ventaIdCero = new Venta();
        ventaIdCero.setCodigo("X002");
        ventaIdCero.setFecha(LocalDateTime.now());
        ventaIdCero.setTotal(new BigDecimal("100"));
        ventaIdCero.setCodMoneda("USD");
        ventaIdCero.setIdCliente(0);

        return Stream.of(
                Arguments.of(null, "venta nula", "venta no puede ser null"),
                Arguments.of(ventaIdNull, "idCliente null", "id de cliente no puede ser null o menor que 1"),
                Arguments.of(ventaIdCero, "idCliente cero", "id de cliente no puede ser null o menor que 1")
        );
    }

    @Test
    @DisplayName("Debe insertar una venta válida con cliente existente")
    void debeInsertarVentaValida() {
        Venta venta = new Venta();
        venta.setCodigo("V001");
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(new BigDecimal("250.50"));
        venta.setCodMoneda("USD");
        venta.setIdCliente(1);

        when(ventaRepository.findByCodigo("V001")).thenReturn(Optional.empty());
        when(ventaRepository.save(venta)).thenReturn(venta);

        Venta resultado = ventaService.crearVenta(venta);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCodigo()).isEqualTo("V001");
        verify(ventaRepository).save(venta);
    }

    @Test
    @DisplayName("Debe lanzar excepción si ya existe una venta con el mismo código")
    void debeLanzarExcepcionSiCodigoExiste() {
        Venta venta = new Venta();
        venta.setCodigo("V003");
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(new BigDecimal("300.00"));
        venta.setCodMoneda("USD");
        venta.setIdCliente(2);

        when(ventaRepository.findByCodigo("V003"))
                .thenReturn(Optional.of(new Venta())); // simulamos duplicado

        assertThatThrownBy(() -> ventaService.crearVenta(venta))
                .isInstanceOf(EntityExistsException.class)
                .hasMessageContaining("Ya existe una venta registrada con ese código");

        verify(ventaRepository, never()).save(any());
    }

    // ---------- UPDATE ----------
    @Test
    @DisplayName("Debe actualizar una venta existente")
    void debeActualizarVentaExistente() {
        Venta existente = new Venta();
        existente.setId(1);
        existente.setCodigo("asd");
        existente.setTotal(new BigDecimal("300"));

        when(ventaRepository.findByCodigo("asd")).thenReturn(Optional.of(existente));
        when(ventaRepository.save(any(Venta.class))).thenReturn(existente);

        Venta cambios = new Venta();
        cambios.setCodigo("asd");
        cambios.setTotal(new BigDecimal("450.99"));
        cambios.setCodMoneda("USD");

        Venta actualizado = ventaService.actualizarVenta("asd", cambios);

        assertThat(actualizado.getTotal()).isEqualByComparingTo("450.99");
        verify(ventaRepository).save(any(Venta.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar venta inexistente")
    void debeLanzarExcepcionAlActualizarInexistente() {
        when(ventaRepository.findByCodigo("asd")).thenReturn(Optional.empty());

        Venta cambios = new Venta();
        cambios.setCodigo("asd");
        cambios.setTotal(new BigDecimal("150"));

        assertThatThrownBy(() -> ventaService.actualizarVenta("asd", cambios))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("no encontrada");
    }

    @Test
    @DisplayName("Debe lanzar excepción si la venta es null al actualizar")
    void debeLanzarExcepcionSiVentaEsNullEnUpdate() {
        assertThatThrownBy(() -> ventaService.actualizarVenta("ABC", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede ser null");

        verify(ventaRepository, never()).save(any());
    }

    @ParameterizedTest(name = "{index} => codigo=''{0}'', mensajeEsperado=''{2}''")
    @MethodSource("proveedorCodigosInvalidos")
    @DisplayName("Debe lanzar excepción ante códigos inválidos en actualizarVenta")
    void debeLanzarExcepcionConCodigosInvalidos(String codigo, Venta venta, String mensajeEsperado) {

        assertThatThrownBy(() -> ventaService.actualizarVenta(codigo, venta))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(mensajeEsperado);

        verify(ventaRepository, never()).save(any());
    }

    private static Stream<Arguments> proveedorCodigosInvalidos() {
        Venta venta1 = new Venta();
        venta1.setCodigo("ABC");

        Venta venta2 = new Venta();
        venta2.setCodigo("COD_ORIGINAL");

        return Stream.of(
                Arguments.of(null, venta1, "null"),
                Arguments.of("   ", venta1, "vacío"),
                Arguments.of("OTRO_CODIGO", venta2, "no puede cambiarse")
        );
    }

    // ---------- DELETE ----------
    @Test
    @DisplayName("Debe eliminar venta existente")
    void debeEliminarVentaExistente() {
        Venta existente = new Venta();
        existente.setCodigo("asd");

        when(ventaRepository.findByCodigo("asd")).thenReturn(Optional.of(existente));

        ventaService.eliminarVenta("asd");

        verify(ventaRepository).delete(existente);
    }

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException al intentar eliminar una venta inexistente")
    void debeLanzarExcepcionAlEliminarInexistente() {
        when(ventaRepository.findByCodigo("asd")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ventaService.eliminarVenta("asd"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("no encontrada");

        verify(ventaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el código es null al eliminar venta")
    void debeLanzarExcepcionSiCodigoEsNull() {
        assertThatThrownBy(() -> ventaService.eliminarVenta(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");

        verify(ventaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el código está vacío o en blanco al eliminar venta")
    void debeLanzarExcepcionSiCodigoEsBlanco() {
        assertThatThrownBy(() -> ventaService.eliminarVenta("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("vacío");

        verify(ventaRepository, never()).delete(any());
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

    @Test
    @DisplayName("Debe listar ventas incluyendo nombre de cliente cuando existe y null cuando no")
    void debeListarVentasConYsinCliente() {
        Venta conCliente = new Venta();
        conCliente.setId(1);
        conCliente.setCodigo("A1");
        conCliente.setIdCliente(10);

        Cliente cli = new Cliente();
        cli.setNombre("Juan Pérez");
        conCliente.setCliente(cli);

        Venta sinCliente = new Venta();
        sinCliente.setId(2);
        sinCliente.setCodigo("A2");
        sinCliente.setIdCliente(20);
        sinCliente.setCliente(null);

        when(ventaRepository.findAll(Sort.by("codigo")))
                .thenReturn(List.of(conCliente, sinCliente));

        List<VentaDTO> resultado = ventaService.listarVentas();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).nombreCliente()).isEqualTo("Juan Pérez");
        assertThat(resultado.get(1).nombreCliente()).isNull();
    }

    // ---------- FIND BY CODIGO ----------

    @Test
    @DisplayName("Debe retornar venta existente al buscar por código")
    void debeRetornarVentaPorCodigoExistente() {
        Venta venta = new Venta();
        venta.setId(1);
        venta.setCodigo("V123");
        venta.setTotal(new BigDecimal("999.99"));
        venta.setIdCliente(7);

        when(ventaRepository.findByCodigo("V123")).thenReturn(Optional.of(venta));

        Venta resultado = ventaService.obtenerVentaByCodigo("V123");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCodigo()).isEqualTo("V123");
        assertThat(resultado.getTotal()).isEqualByComparingTo("999.99");
        verify(ventaRepository).findByCodigo("V123");
    }

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException cuando no existe la venta")
    void debeLanzarEntityNotFoundSiNoExiste() {
        when(ventaRepository.findByCodigo("X")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ventaService.obtenerVentaByCodigo("X"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Venta no encontrada");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el código es null")
    void debeLanzarSiCodigoEsNull() {
        assertThatThrownBy(() -> ventaService.obtenerVentaByCodigo(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el código está vacío o en blanco")
    void debeLanzarSiCodigoEsBlanco() {
        assertThatThrownBy(() -> ventaService.obtenerVentaByCodigo("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("vacio");
    }

}
