package gm.tienda_libros.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Ventas")
public class Venta extends BaseEntity{
    @Column(nullable = false, length = 10, unique = true)
    private String codigo;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, length = 3)
    private String codMoneda;

    @Column(nullable = false)
    private Integer idCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCliente", referencedColumnName = "id", insertable = false, updatable = false)
    private Cliente cliente;
}
