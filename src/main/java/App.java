import Service.CsvPeliculaService;
import Service.FileUsuarioService;
import Service.PeliculaService;
import Service.UsuarioService;

public class App {
    public static void main(String[] args) {
        PeliculaService ps = new CsvPeliculaService("peliculas.csv");
        UsuarioService us = new FileUsuarioService("usuarios.csv");
        (new UI.PantallaPrincipal(ps,us)).start();
    }
}
