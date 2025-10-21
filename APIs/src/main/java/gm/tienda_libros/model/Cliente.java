package gm.tienda_libros.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Clientes")
public class Cliente  extends  BaseEntity{
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50, message = "El nombre tiene un maximo de 50 caracteres")
    @Column(nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "El email no puede estar vacío")
    @Size(max = 50, message = "El email tiene un maximo de 50 caracteres")
    @Email(message = "El email debe tener un formato válido")
    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @NotBlank(message = "El telefono no puede estar vacío")
    @Size(max = 50, message = "El telefono tiene un maximo de 50 caracteres")
    @Column(nullable = false, length = 50)
    private String telefono;
}
