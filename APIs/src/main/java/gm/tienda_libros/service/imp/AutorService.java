package gm.tienda_libros.service.imp;

import gm.tienda_libros.model.Autor;
import gm.tienda_libros.repository.AutorRepository;
import gm.tienda_libros.service.IAutorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutorService implements IAutorService {
    private final AutorRepository  autorRepository;

    public AutorService (AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    @Override
    public List<Autor> listarAutores() {
        return autorRepository.findAll(Sort.by("nombre"));
    }

    @Override
    public Autor obtenerAutorById(Integer id) {
        if(id == null || id <= 0){
            throw new IllegalArgumentException("El id no puede ser null o menor igual a 0");
        }

        return autorRepository.findById(id).orElseThrow(
                ()->new EntityNotFoundException("No seencontro el autor con id: " + id)
        );
    }

    @Override
    public Autor crearAutor(Autor autor) {
        return null;
    }

    @Override
    public Autor actualizarAutor(Integer id, Autor autor) {
        return null;
    }

    @Override
    public void eliminarAutor(Integer id) {
        if(id == null || id <= 0){
            throw new IllegalArgumentException("El id no puede ser null o menor igual a 0");
        }

        Autor autorBuscado = obtenerAutorById(id);

        autorRepository.delete(autorBuscado);
    }

    @Override
    public List<Autor> buscarAutoresNombre(String nombre) {
        if(nombre == null || nombre.isBlank()){
            throw new IllegalArgumentException("El nombre no puede ser null o estar vacio");
        }

        return autorRepository.findByNombreContainingIgnoreCaseOrderByNombre(nombre);
    }
}
