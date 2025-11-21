package gm.tienda.libros.service.imp;

import gm.tienda.libros.model.Autor;
import gm.tienda.libros.repository.AutorRepository;
import gm.tienda.libros.service.IAutorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
    public Autor obtenerAutorPorId(Integer id) {

        Objects.requireNonNull(id, "El id no puede ser null");

        if(id <= 0){
            throw new IllegalArgumentException("El id no puede ser null o menor igual a 0");
        }

        return autorRepository.findById(id).orElseThrow(
                ()->new EntityNotFoundException("No se encontró el autor con id: " + id)
        );
    }

    @Override
    public Autor crearAutor(Autor autor) {

        if(autor == null){
            throw new IllegalArgumentException("El autor no puede ser null.");
        }

        return autorRepository.save(autor);
    }

    @Override
    public Autor actualizarAutor(Integer id, Autor autor) {
        if(autor == null){
            throw new IllegalArgumentException("El autor no puede ser null");
        }

        Autor autorExistente = autorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el autor con id: " + id));

        //Modifico el resto de campos
        autorExistente.setNombre(autor.getNombre());
        autorExistente.setApellidos(autor.getApellidos());

        return autorRepository.save(autorExistente);
    }

    @Override
    public void eliminarAutor(Integer id) {
        if(id == null || id <= 0){
            throw new IllegalArgumentException("El id no puede ser null o menor igual a 0");
        }

        Autor autorBuscado = obtenerAutorPorId(id);

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
