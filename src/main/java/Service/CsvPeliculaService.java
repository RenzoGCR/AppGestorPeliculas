package Service;

import Model.Pelicula;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class CsvPeliculaService implements PeliculaService {
    private List<Pelicula> peliculas;
    private String archivo;
    //variable para guardar el ultimo id, por defecto -1 (no hay nada)
    private static int usuarioId =-1;
    private static Logger logger= Logger.getLogger(CsvPeliculaService.class.getName());

    public CsvPeliculaService(String csvFile) {
        this.archivo = csvFile;
        //al instanciar se crea la lista con los datos CSV (si existen)
        this.peliculas = findAll();
        // Actualizar lastId al máximo id encontrado en el CSV
        if (!this.peliculas.isEmpty()) {
            usuarioId = this.peliculas.stream().mapToInt(Pelicula::getId).max().orElse(-1);
        }
    }

    public static void setLastId(int lastId) {
        CsvPeliculaService.usuarioId = lastId;
    }

    //metodo para añadir todos las peliculas del csv en una lista de objetos Pelicula
    @Override
    public List<Pelicula> findAll() {
        var salida = new ArrayList<Pelicula>();

        logger.info("Iniciando lista de peliculas");
        try(BufferedReader br = new BufferedReader(new FileReader(("AppGestorPeliculas/AppGestorPeliculas/src/main/java/resources/"+archivo)))){
            String linea;
            while((linea = br.readLine()) != null){

                String[] trozos = linea.split(",");
                if(trozos.length==7){
                    Pelicula nuevaPelicula = new Pelicula();
                    nuevaPelicula.setId(Integer.parseInt(trozos[0]));
                    nuevaPelicula.setTitulo(trozos[1]);
                    nuevaPelicula.setAño(Integer.parseInt(trozos[2]));
                    nuevaPelicula.setDirector(trozos[3]);
                    nuevaPelicula.setDescripcion(trozos[4]);
                    nuevaPelicula.setGenero(trozos[5]);
                    nuevaPelicula.setImagen(trozos[6]);
                    salida.add(nuevaPelicula);
                }
            }
        } catch (FileNotFoundException e) {
            logger.warning("Archivo CSV no encontrado: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Error al leer/parsear el archivo CSV: " + e.getMessage(), e);
        }
        return salida;
    }

    //metodo para escribir los objetos Pelicula que estan el el arrayList en un documento csv
    @Override
    public Optional<Pelicula> save(Pelicula pelicula){
        logger.info("Abriendo el archivo para escribir");
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("AppGestorPeliculas/AppGestorPeliculas/src/main/java/resources/"+archivo,true))){
            usuarioId++;
            pelicula.setId(usuarioId);
            logger.info("Actualizando id: "+ usuarioId);

            bw.write(pelicula.toString());
            bw.newLine();
            logger.info("Pelicula guardada: "+pelicula.getTitulo());

        } catch (IOException e) {
            throw new RuntimeException("Error al escribir en el archivo CSV: " + e.getMessage(), e);
        }
        return Optional.of(pelicula);
    }

    //metodo para añadir un objeto Pelicula nuevo en el arrayList
    @Override
    public void addPelicula(Pelicula nuevaPelicula) {
        // Asignar ID antes de añadir a la lista
        usuarioId++;
        nuevaPelicula.setId(usuarioId);
        this.peliculas.add(nuevaPelicula);
        logger.info("Pelicula añadida a la lista: " + nuevaPelicula.getTitulo());
        // Después de añadir o eliminar, se debería llamar a saveAll(this.peliculas) para persistir el cambio.
    }

    //metodo para borrar un Pelicula de el arrayList de Pelicula a partir del titulo
    @Override
    public void deletePelicula(String tituloPelicula) {
        boolean eliminado = this.peliculas.removeIf(pelicula -> pelicula.getTitulo().equalsIgnoreCase(tituloPelicula));
        if (eliminado) {
            logger.info("Pelicula eliminada de la lista: " + tituloPelicula);
            // Después de añadir o eliminar, se debería llamar a saveAll(this.peliculas) para persistir el cambio.
        } else {
            logger.warning("No se encontró la pelicula para eliminar: " + tituloPelicula);
        }
    }
    public void saveAll(List<Pelicula> peliculas){
        logger.info("Guardando lista completa de peliculas al CSV");
        // El 'false' en FileWriter indica que sobrescribirá el archivo (no appending)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("AppGestorPeliculas/AppGestorPeliculas/src/main/java/resources/" + archivo, false))) {
            for (Pelicula p : peliculas) {
                bw.write(p.toString());
                bw.newLine();
            }
            logger.info("Lista completa guardada.");
            // Actualizar la lista interna después de guardar
            this.peliculas = new ArrayList<>(peliculas);
        } catch (IOException e) {
            throw new RuntimeException("Error al sobrescribir el archivo CSV: " + e.getMessage(), e);
        }
    }

}
