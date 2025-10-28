package Model;

import lombok.Data;

@Data
public class Pelicula {
    private Integer id;
    private String titulo;
    private int año;
    private String director;
    private String descripcion;
    private String genero;
    private String imagen;
    private int idUsuario;

    @Override
    public String toString() {
        return id +","+titulo+","+año+","+director+","+descripcion+","+genero+","+imagen+","+idUsuario;
    }
}
