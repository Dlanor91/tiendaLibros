package gm.tienda_libros.service.imp;

import gm.tienda_libros.model.GeneroLiterario;
import gm.tienda_libros.repository.GeneroLiterarioRepository;
import gm.tienda_libros.service.IGeneroLiterarioService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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
        if (codigo == null || codigo.trim().isEmpty()){
            throw new IllegalArgumentException("El codigo ingresado es null o vacío");
        }

        return generoLiterarioRepository.findByCodigo(codigo).orElseThrow(
                ()-> new EntityNotFoundException("Genero literario no encontrado")
        );
    }

    @Override
    public GeneroLiterario agregarGeneroLiterario(GeneroLiterario generoLiterario) {
        return null;
    }

    @Override
    public GeneroLiterario actualizarGeneroLiterario(String codigo, GeneroLiterario generoLiterario) {
        if (generoLiterario == null){
            throw new IllegalArgumentException("El genero debe ser enviado para actualizar");
        }

        if (codigo == null || codigo.trim().isEmpty()){
            throw new IllegalArgumentException("El codigo ingresado es null o vacío");
        }

        GeneroLiterario buscarGeneroliterario = generoLiterarioRepository.findByCodigo(codigo).orElseThrow(
                ()-> new EntityNotFoundException("No existe un genero con ese codigo ingresado")
        );

        buscarGeneroliterario.setNombre(generoLiterario.getNombre());

        return generoLiterarioRepository.save(buscarGeneroliterario);
    }

    @Override
    public void eliminarGeneroLiterario(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()){
            throw new IllegalArgumentException("El codigo ingresado es null o vacío");
        }

        GeneroLiterario buscarGeneroliterario = generoLiterarioRepository.findByCodigo(codigo).orElseThrow(
                ()-> new EntityNotFoundException("No existe el género con ese codigo ingresado")
        );

        generoLiterarioRepository.delete(buscarGeneroliterario);
    }
}
