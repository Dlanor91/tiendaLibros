package gm.tienda_libros.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Autores")
public class Autor extends BaseEntity{
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50, message = "El nombre tiene un maximo de 50 caracteres")
    @Column(nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "Los apellidos no puede estar vacío")
    @Size(max = 50, message = "Los apellidos tiene un maximo de 50 caracteres")
    @Column(nullable = false, length = 50)
    private String apellidos;

    @ManyToMany(mappedBy = "autores")
    private List<Libro> libros;
}
