package gm.tienda_libros.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @NotBlank(message = "El código no puede estar vacío")
    @Size(max = 10, message = "El codigo tiene un maximo de 10 caracteres")
    @Column(nullable = false, length = 10, unique = true)
    private String codigo;

    @NotNull(message = "La fecha no puede ser null")
    @Column(nullable = false)
    private LocalDateTime fecha;

    @NotNull(message = "El total no puede ser null")
    @Positive(message = "El total debe seer mayor que 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @NotBlank(message = "El código de moneda no puede estar vacío")
    @Size(max = 3, message = "El codigo de moneda tiene un maximo de 3 caracteres")
    @Column(nullable = false, length = 3)
    private String codMoneda;

    @NotNull(message = "El id del cliente no puede ser null")
    @Min(value = 1, message = "El id de cliente debe ser mayor que 0")
    @Column(nullable = false)
    private Integer idCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCliente", referencedColumnName = "id", insertable = false, updatable = false)
    private Cliente cliente;
}
