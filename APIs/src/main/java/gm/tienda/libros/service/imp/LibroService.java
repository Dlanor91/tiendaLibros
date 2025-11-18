package gm.tienda.libros.service.imp;

import gm.tienda.libros.model.Libro;
import gm.tienda.libros.repository.LibroRepository;
import gm.tienda.libros.service.ILibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService implements ILibroService {
    @Autowired
    private LibroRepository libroRepository;

    @Override
    public List<Libro> listarLibros() {
        return libroRepository.findAll();
    }

    @Override
    public Libro buscarLibroPorId(Integer idLibro) {
        Libro libro = libroRepository.findById(idLibro).orElse(null);

        return libro;
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
