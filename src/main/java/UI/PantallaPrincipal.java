package UI;

import Model.Pelicula;
import Model.Usuario;
import Service.ContextService;
import Service.CsvPeliculaService;
import Service.PeliculaService;
import Service.UsuarioService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class PantallaPrincipal extends javax.swing.JFrame {
    private JPanel panel1;
    private JLabel Imagen;
    private JLabel titulo;
    private JLabel director;
    private JLabel año;
    private JPanel contenedorPeliculas;
    private JPanel jPanelPelicula;
    private PeliculaService peliculaservice;
    private UsuarioService usuarioservice;
    private ArrayList<Pelicula> peliculas = new ArrayList<>();

    /* Es necesario que este accesible para poder modificarlo */
    private JMenuItem menuItemAñadir;
    private JMenuItem menuItemEliminar;

    public PantallaPrincipal(PeliculaService ps, UsuarioService us) {
        peliculaservice = ps;
        usuarioservice = us;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Libreria de Peliculas");
        this.setResizable(false);
        this.setSize(800, 800);
        this.setLocationRelativeTo(null);
        this.setContentPane(panel1);

        /* Añadir menu de forma programática */
        JMenuBar menuBar = PrepareMenuBar();
        panel1.add(menuBar, BorderLayout.NORTH);


        //Añadido de la imagen de la aplicacion al iniciarla
        String rutaImagen = "iconoNetflix.png";
        URL urlImagen = getClass().getClassLoader().getResource(rutaImagen);
        Image icono = new ImageIcon(urlImagen).getImage();
        this.setIconImage(icono);

        loadPeliculas();
    }

    private void loadPeliculas() {
        // 1. Limpiar el contenedor actual antes de añadir nuevos elementos
        contenedorPeliculas.removeAll();

        //cambiar el layout a GridLayout con 2 columnas
        contenedorPeliculas.setLayout(new GridLayout(0, 2, 10, 10));

        // 2. Obtener TODAS las peliculas
        ArrayList<Pelicula> todasPeliculas = (ArrayList<Pelicula>) peliculaservice.findAll();

        // 3. Obtener el usuario activo del ContextService
        ContextService.getInstance().getItem("usuarioActivo")
                .ifPresent(userObject -> {
                    // El Optional tiene un valor. Lo casteamos a Usuario.
                    Usuario usuarioActivo = (Usuario) userObject;

                    // 4. Filtrar la lista: Solo peliculas cuyo idUsuario coincida con el ID del usuario activo
                    ArrayList<Pelicula> peliculasFiltradas = (ArrayList<Pelicula>) todasPeliculas.stream()
                            .filter(p -> p.getIdUsuario() == usuarioActivo.getId())
                            .collect(Collectors.toList());

                    // 5. Cargar las películas filtradas en la UI
                    cargarPeliculasEnUI(peliculasFiltradas); // Llama a tu metodo auxiliar para pintar en la UI
                });
        // 6. Repintar la UI
        contenedorPeliculas.revalidate();
        contenedorPeliculas.repaint();
    }

    private void cargarPeliculasEnUI(ArrayList<Pelicula> peliculasACargar) {
        // Verificar si hay películas para evitar bucles innecesarios
        if (peliculasACargar == null || peliculasACargar.isEmpty()) {
            // Opcional: Mostrar un mensaje si no hay películas para el usuario
            JLabel mensaje = new JLabel("No tienes películas añadidas.", SwingConstants.CENTER);
            contenedorPeliculas.add(mensaje);
            return; // Salir del metodo
        }

        // Iterar sobre cada película filtrada
        for (Pelicula pelicula : peliculasACargar) {

            // 1. Crear el componente visual reutilizable para la película actual.
            // Se asume que este metodo auxiliar maneja la creación y configuración
            // de etiquetas e imágenes para una sola Pelicula.
            JPanel panelPelicula = crearPanelPelicula(pelicula);

            // 2. Adjuntar el Listener de Clic al panel.
            addClickListenerToPanel(panelPelicula, pelicula); // Llama al nuevo metodo

            // 3. Añadir el componente al contenedor principal (que debe ser FlowLayout).
            contenedorPeliculas.add(panelPelicula);
        }
    }

    private void addClickListenerToPanel(JPanel panel, Pelicula pelicula) {
        // Configura el panel para que cambie de color al pasar el mouse (opcional)
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Lógica similar a table1.getSelectionModel().addListSelectionListener:

                // 1. Guardar el objeto Pelicula seleccionado en el ContextService.
                ContextService.getInstance().addItem("peliculaSeleccionada", pelicula);

                // 2. Mostrar la ventana de detalles (JDialog).
                // Asumiendo que 'Detalles' recibe la referencia de 'PantallaPrincipal' (this) como su Frame padre
                // y que el constructor ya sabe cómo obtener 'peliculaSeleccionada' del ContextService.
                (new Detalles(PantallaPrincipal.this)).setVisible(true);
            }
        });
    }

    private JPanel crearPanelPelicula(Pelicula pelicula) {
        //Se deben usar las variables imagen, titulo, etc para crear un nuevo panel

        JPanel panel = new JPanel(new BorderLayout()); // Panel individual de la película
        panel.setPreferredSize(new Dimension(200, 300)); // Establecer un tamaño fijo
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel lblTitulo = new JLabel(pelicula.getTitulo(), SwingConstants.CENTER);
        JLabel lblDirector = new JLabel(pelicula.getDirector());
        JLabel lblAño = new JLabel(String.valueOf(pelicula.getAño()));

        // Simulación de imagen (en la práctica se usaría ImageIcon)
        JLabel lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);

        String imageUrlString = pelicula.getImagen();

        // Definimos las dimensiones deseadas para la imagen en el panel
        final int WIDTH = 180;
        final int HEIGHT = 250;

        if (imageUrlString != null && !imageUrlString.isEmpty()) {
            try {
                // 1. Crear un objeto URL a partir de la cadena HTTPS
                java.net.URL imageUrl = new java.net.URL(imageUrlString);

                // 2. Crear el ImageIcon. Swing descarga automáticamente la imagen.
                ImageIcon originalIcon = new ImageIcon(imageUrl);
                Image image = originalIcon.getImage();

                int ancho = image.getWidth(lblImagen);

                // Verificamos si la imagen se cargó correctamente (el ancho debe ser > 0)
                if (ancho > 0) {

                    // 3. Escalar la imagen para que encaje en el JLabel
                    Image scaledImage = image.getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);

                    // 4. Asignar el icono escalado al JLabel
                    lblImagen.setIcon(new ImageIcon(scaledImage));
                    lblImagen.setText(null); // Borrar el texto
                } else {
                    // Si el ancho es <= 0 (falla al descargar o imagen no válida)
                    lblImagen.setText("<html><center>ERROR: Imagen en URL vacía o no válida.</center></html>");
                }
            } catch (java.net.MalformedURLException e) {
                // Error si la URL no tiene el formato correcto (ej. le falta "https://")
                lblImagen.setText("<html><center>URL INVÁLIDA:<br>" + imageUrlString + "</center></html>");
            } catch (Exception e) {
                // Captura otros errores, como problemas de conexión o descarga
                lblImagen.setText("<html><center>ERROR DE CONEXIÓN o DESCARGA.</center></html>");
            }
        } else {
            lblImagen.setText("SIN RUTA DE IMAGEN");
        }

        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.add(lblDirector);
        infoPanel.add(lblAño);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblImagen, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JMenuBar PrepareMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu jMenuInicio = new JMenu("Inicio");
        JMenuItem menuItemLogin = new JMenuItem("Login");
        // es un atributo de la clase, no hay que hacer new
        menuItemAñadir = new JMenuItem("Añadir");
        menuItemAñadir.setEnabled(false);
        menuItemEliminar = new JMenuItem("Eliminar");
        menuItemEliminar.setEnabled(false);
        JMenuItem menuItemSalir = new JMenuItem("Salir");

        menuBar.add(jMenuInicio);
        jMenuInicio.add(menuItemLogin);
        jMenuInicio.addSeparator();
        jMenuInicio.add(menuItemAñadir);
        jMenuInicio.addSeparator();
        jMenuInicio.add(menuItemEliminar);
        jMenuInicio.addSeparator();
        jMenuInicio.add(menuItemSalir);

        /* Eventos del menú */
        menuItemLogin.addActionListener(e -> {

            (new Login(this, usuarioservice)).setVisible(true);

            // Como es modal, se espera la ejecución hasta que se cierre

            ContextService.getInstance().getItem("usuarioActivo").ifPresent((_) -> {
                menuItemAñadir.setEnabled(true);
                menuItemEliminar.setEnabled(true);
                loadPeliculas();
            });
        });

        menuItemSalir.addActionListener(e -> {
            System.exit(0);
        });

        menuItemAñadir.addActionListener(e -> {
            // Añadir nueva pelicula

            (new CreadorFormulario(this, peliculaservice)).setVisible(true);
            //metodo para cargar las peliculas en el contenedorPeliculas
            loadPeliculas();
        });
        menuItemEliminar.addActionListener(e -> {
            //elimino la pelicula buscandola por el titulo y luego recargo el csv.
            //1. Solicitar el nombre de la pelicula a eliminar
            String tituloAEliminar = JOptionPane.showInputDialog(this, "Introduce el titulo de la pelicula a eliminar",
                    "Eliminar Pelicula", JOptionPane.QUESTION_MESSAGE
            );
            //2. Procesar la eliminación solo si el usuario ingresó un título
            if (tituloAEliminar != null && !tituloAEliminar.trim().isEmpty()) {

                // 3. Llamar al metodo del servicio para eliminar de la lista en memoria
                try {
                    peliculaservice.deletePelicula(tituloAEliminar.trim());

                    // 4. Persistir la lista completa después de la eliminación
                    // (Se asume que peliculasService es una instancia de CsvPeliculaService
                    // y que tiene el metodo getPeliculas()).
                    ((CsvPeliculaService) peliculaservice).saveAll(((CsvPeliculaService) peliculaservice).getPeliculas());

                    // 5. Notificar éxito (Opcional, pero útil)
                    JOptionPane.showMessageDialog(this,
                            "Película '" + tituloAEliminar.trim() + "' eliminada y guardada.",
                            "Eliminación Exitosa",
                            JOptionPane.INFORMATION_MESSAGE);

                    // 6. Recargar la interfaz de usuario para mostrar los cambios
                    loadPeliculas();

                } catch (Exception ex) {
                    // Manejar errores (ej. si el archivo no se pudo escribir)
                    JOptionPane.showMessageDialog(this,
                            "Error al guardar los cambios: " + ex.getMessage(),
                            "Error de Persistencia",
                            JOptionPane.ERROR_MESSAGE);
                }

            } else if (tituloAEliminar != null) {
                // Mensaje si el usuario presiona OK pero deja el campo vacío
                JOptionPane.showMessageDialog(this, "El título no puede estar vacío.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        return menuBar;
    }

    public void start() {
        this.setVisible(true);
    }

}
