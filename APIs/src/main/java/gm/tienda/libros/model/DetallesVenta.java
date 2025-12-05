package gm.tienda.libros.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotNull(message = "La cantidad no puede ser null")
    @Column(nullable = false)
    @Min(value = 1, message = "La cantidad minima es 1")
    private Integer cantidad;

    @NotNull(message = "El precio unitario no puede ser null")
    @Positive(message = "El precio debe ser mayor que 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @NotBlank(message = "La codigo de moneda no puede ser null")
    @Column(nullable = false, length = 3)
    @Size(max = 3, message = "El cod de Moneda tiene un maximo de 3 caracteres")
    private String codMoneda;

    @NotNull(message = "El subtotal no puede ser null")
    @Positive(message = "El subtotal debe ser mayor que 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @NotBlank(message = "El isbn del Libro no puede ser null")
    @Column(nullable = false, length = 20)
    @Size(max = 20, message = "El isbn del Libro tiene un maximo de 20 caracteres")
    private String isbnLibro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbnLibro", referencedColumnName = "isbn", insertable = false, updatable = false)
    private Libro libro;

    @NotBlank(message = "El cod de Venta no puede ser null")
    @Column(nullable = false, length = 10)
    @Size(max = 10, message = "El codigo de Venta tiene un maximo de 10 caracteres")
    private String codVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codVenta", referencedColumnName = "codigo", insertable = false,updatable = false)
    private Venta venta;
}
