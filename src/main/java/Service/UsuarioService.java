package Service;

import Model.Pelicula;
import Model.Usuario;

import java.util.ArrayList;
import java.util.Optional;

public interface UsuarioService {
    Optional<Usuario> validate(String usuario, String password);

}
