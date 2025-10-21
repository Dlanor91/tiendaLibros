package gm.tienda_libros.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GenerosLiterarios")
public class GeneroLiterario extends BaseEntity{
    @NotBlank(message = "El nombre no puede estar en blanco")
    @Size(max = 50, message = "El máximo de caracteres para nombre es 50")
    @Column(nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "El codigo no puede estar en blanco")
    @Size(max = 3, message = "El máximo de caracteres para codigo es 3")
    @Column(nullable = false, unique = true, length = 3)
    String codigo;
}
