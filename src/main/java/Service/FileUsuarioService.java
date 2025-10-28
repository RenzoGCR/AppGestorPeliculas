package Service;

import Model.Pelicula;
import Model.Usuario;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

public class FileUsuarioService implements UsuarioService{
    private static int usuarioId =-1;
    private String archivo;
    private ArrayList<Usuario> usuarios;
    private static Logger logger= Logger.getLogger(FileUsuarioService.class.getName());
    

    public FileUsuarioService(String archivo) {
        this.archivo = archivo;
        this.usuarios=loadUsersFromFile();
        // Al instanciar, inicializamos la lista con los datos del CSV (si existen)
        if (!this.usuarios.isEmpty()) {
            usuarioId = this.usuarios.stream().mapToInt(Usuario::getId).max().orElse(-1);
        }
    }
    public static void setUsuarioId(int usuarioId) {
        FileUsuarioService.usuarioId=usuarioId;
    }

    // Metodo auxiliar para cargar usuarios, haciendo que el constructor sea más limpio
    private ArrayList<Usuario> loadUsersFromFile() {
        var salida = new ArrayList<Usuario>();
        logger.info("Cargando usuarios desde el archivo: " + archivo);
        // La ruta se mantiene igual que en el código original
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/"+archivo))){
            String linea;
            while((linea = br.readLine()) != null){
                String[] trozos = linea.split(",");
                if(trozos.length == 3){
                    Usuario nuevoUsuario = new Usuario();
                    nuevoUsuario.setId(Integer.parseInt(trozos[0]));
                    nuevoUsuario.setEmail(trozos[1]);
                    nuevoUsuario.setContraseña(trozos[2]);
                    salida.add(nuevoUsuario);
                }
            }
        } catch (FileNotFoundException e) {
            logger.warning("Archivo de usuarios no encontrado. Se iniciará sin usuarios. Ruta: " + archivo);
            // No lanzar excepción, permitir que la lista quede vacía.
        } catch (IOException | NumberFormatException e) {
            // Capturar también NumberFormatException por si el ID no es un entero
            throw new RuntimeException("Error al leer o parsear el archivo de usuarios: " + e.getMessage(), e);
        }
        return salida;
    }

    @Override
    public Optional<Usuario> validate(String usuario, String password) {
        // Usar un Stream para buscar de forma más funcional y limpia
        Optional<Usuario> userFound = this.usuarios.stream()
                .filter(u -> u.getEmail().equals(usuario) && u.getContraseña().equals(password))
                .findFirst();

        if (userFound.isPresent()) {
            // Usar nivel INFO en lugar de WARNING para un evento exitoso
            logger.info("Validación exitosa para el usuario: " + usuario);
        } else {
            logger.warning("Fallo de validación para el usuario: " + usuario);
        }
        return userFound;
    }
}
