package gm.tienda_libros.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Libro extends BaseEntity{
    String nombre;
    String autor;
    Double precio;
    Integer cantidad;
    String descripcion;
    LocalDate fechaPublicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCategoria", referencedColumnName = "id", insertable = false, updatable = false)
    private Categoria categoria;
}
