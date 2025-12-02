package gm.tienda.libros.service.imp;

import gm.tienda.libros.model.GeneroLiterario;
import gm.tienda.libros.repository.GeneroLiterarioRepository;
import gm.tienda.libros.service.IGeneroLiterarioService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GeneroLiterarioService implements IGeneroLiterarioService {

    private final GeneroLiterarioRepository generoLiterarioRepository;

    public GeneroLiterarioService(GeneroLiterarioRepository generoLiterarioRepository) {
        this.generoLiterarioRepository = generoLiterarioRepository;
    }

    @Override
    public List<GeneroLiterario> listarGenerosLiterarios() {
        return generoLiterarioRepository.findAll(Sort.by("codigo"));
    }

    @Override
    public GeneroLiterario obtenerGeneroLiterarioByCodigo(String codigo) {
        validarNoNull(codigo);

        validarNoVacio(codigo);

        return generoLiterarioRepository.findByCodigo(codigo).orElseThrow(
                ()-> new EntityNotFoundException("Genero literario no encontrado")
        );
    }

    @Override
    public GeneroLiterario agregarGeneroLiterario(GeneroLiterario generoLiterario) {
        Objects.requireNonNull(generoLiterario, "El genero debe ser enviado para actualizar");

        boolean existeGeneroLiterario = generoLiterarioRepository.findByCodigo(generoLiterario.getCodigo()).isPresent();
        if (existeGeneroLiterario){
            throw new EntityExistsException("Ya existe ese código de género literario");
        }

        generoLiterario.setCodigo(generoLiterario.getCodigo().toUpperCase());

        return  generoLiterarioRepository.save(generoLiterario);
    }

    @Override
    public GeneroLiterario actualizarGeneroLiterario(String codigo, GeneroLiterario generoLiterario) {
        validarNoNull(generoLiterario);

        validarNoNull(codigo);

        validarNoVacio(codigo);

        GeneroLiterario buscarGeneroliterario = generoLiterarioRepository.findByCodigo(codigo).orElseThrow(
                ()-> new EntityNotFoundException("No encontrado un genero con ese codigo ingresado")
        );

        buscarGeneroliterario.setNombre(generoLiterario.getNombre());

        return generoLiterarioRepository.save(buscarGeneroliterario);
    }

    @Override
    public void eliminarGeneroLiterario(String codigo) {
        validarNoNull(codigo);

        validarNoVacio(codigo);

        GeneroLiterario buscarGeneroliterario = generoLiterarioRepository.findByCodigo(codigo).orElseThrow(
                ()-> new EntityNotFoundException("No encontrado el género con ese codigo ingresado")
        );

        generoLiterarioRepository.delete(buscarGeneroliterario);
    }

    private void validarNoNull(Object propiedad) {
        Objects.requireNonNull(propiedad, "El " + propiedad +" ingresado no puede ser null");
    }

    private void validarNoVacio(String propiedad) {
        if (propiedad.trim().isBlank()) {
            throw new IllegalArgumentException("El campo " + propiedad + " no puede estar vacío");
        }
    }
}
