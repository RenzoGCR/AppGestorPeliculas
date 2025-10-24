package Service;

import Model.Pelicula;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface PeliculaService {
    //metodo para mostrar todas las peliculas
    public List<Pelicula> findAll();

    //metodo para añadir una pelicula al CSV
    Optional<Pelicula> save(Pelicula pelicula);

    //metodo para añadir una Pelicula a la lista en memoria
    public void addPelicula(Pelicula nuevaPelicula);

    //metodo para eliminar una pelicula por el titulo
    public void deletePelicula(String tituloPelicula);

    //metodo para guardar la lista completa al CSV
    public void saveAll(List<Pelicula> peliculas);
}
