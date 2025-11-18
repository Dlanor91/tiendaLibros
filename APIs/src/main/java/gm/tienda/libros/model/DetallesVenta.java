package gm.tienda.libros.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "DetallesVenta")
public class DetallesVenta extends BaseEntity{
    @Column(nullable = false)
    @Min(0)
    private Integer cantidad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, length = 3)
    private String codMoneda;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, length = 20)
    private String isbnLibro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbnLibro", referencedColumnName = "isbn", insertable = false, updatable = false)
    private Libro libro;

    @Column(nullable = false, length = 10)
    private String codVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codVenta", referencedColumnName = "codigo", insertable = false,updatable = false)
    private Venta venta;
}
