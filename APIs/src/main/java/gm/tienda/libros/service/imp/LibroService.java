package gm.tienda.libros.service.imp;

import gm.tienda.libros.model.Libro;
import gm.tienda.libros.repository.LibroRepository;
import gm.tienda.libros.service.ILibroService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService implements ILibroService {

    private final LibroRepository libroRepository;

    public LibroService (LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Override
    public List<Libro> listarLibros() {
        return libroRepository.findAll();
    }

    @Override
    public Libro buscarLibroPorId(Integer idLibro) {
        if (idLibro == null || idLibro <= 0) {
            throw new IllegalArgumentException("El ID del libro debe ser mayor que cero.");
        }

        return libroRepository.findById(idLibro)
                .orElseThrow(() -> new EntityNotFoundException("Libro no encontrado con ID: " + idLibro));
    }

    @Override
    public void upsertLibro(Libro libro) {
        libroRepository.save(libro);
    }

    @Override
    public boolean eliminarLibro(Integer idLibro) {
        Libro libro = buscarLibroPorId(idLibro);

        if(libro != null)
        {
            libroRepository.deleteById(idLibro);
            return true;
        }

        return false;
    }
}
