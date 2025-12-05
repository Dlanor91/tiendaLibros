package gm.tienda.libros.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotBlank(message = "El isbn no puede estar vacío")
    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El codMoneda no puede estar vacío")
    @Column(nullable = false, length = 3)
    private String codMoneda;

    @NotNull(message = "El precio no puede estar vacío")
    @Positive(message = "El precio debe ser mayor que 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull(message = "El stock no puede estar vacío")
    @Positive(message = "El stock debe seer mayor que 0")
    @Column(nullable = false)
    @Min(0)
    private Integer stock;

    @NotBlank(message = "El codigo de descripcion no puede estar vacío")
    @Column(nullable = false, length = 250)
    private String descripcion;

    @NotNull(message = "La fechaPublicacion no puede estar vacío")
    @Column(nullable = false)
    private LocalDate fechaPublicacion;

    @NotBlank(message = "El codigo de genero literario no puede estar vacío")
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
