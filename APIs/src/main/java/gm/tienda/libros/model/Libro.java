package gm.tienda.libros.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "Libros")
public class Libro extends BaseEntity{
    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 3)
    private String codMoneda;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    @Min(0)
    private Integer stock;

    @Column(nullable = false, length = 250)
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fechaPublicacion;

    @Column(name = "codGeneroLiterario", nullable = false, length = 3)
    private String codGeneroLiterario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codGeneroLiterario", referencedColumnName = "codigo", insertable = false, updatable = false)
    private GeneroLiterario generoLiterario;

    //Relacion Muchos a Muchos
    @ManyToMany
    @JoinTable(
            name = "LibroAutor",
            joinColumns = @JoinColumn(name = "idLibro"),
            inverseJoinColumns = @JoinColumn(name = "idAutor")
    )
    private List<Autor> autores;
}
