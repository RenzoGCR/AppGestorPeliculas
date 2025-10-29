package UI;

import Model.Pelicula;
import Model.Usuario;
import Service.ContextService;
import Service.CsvPeliculaService;
import Service.PeliculaService;

import javax.swing.*;
import java.awt.event.*;

public class CreadorFormulario extends JDialog{
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfTitulo;
    private JTextField tfDirector;
    private JSpinner spinnerAño;
    private JTextArea taDescripcion;
    private JTextField tfRutaArchivo;
    private JPanel contentPanel;
    private JTextField tfGenero;
    private PeliculaService peliculaService;

    public CreadorFormulario(PeliculaService ps) {
        peliculaService = ps;

        setContentPane(contentPanel);
        setModalityType(ModalityType.APPLICATION_MODAL);
        getRootPane().setDefaultButton(buttonOK);
        setResizable(false);

        spinnerAño.setModel(new SpinnerNumberModel(1990, 1974, 2025, 1));

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pack();
    }

    private void onOK() {

        Pelicula pelicula = new Pelicula();

        pelicula.setTitulo( tfTitulo.getText());
        pelicula.setAño((Integer) spinnerAño.getValue());
        pelicula.setDirector( tfDirector.getText() );
        pelicula.setDescripcion(taDescripcion.getText());
        pelicula.setImagen(tfRutaArchivo.getText());
        pelicula.setGenero(tfGenero.getText());
        pelicula.setId(0);

        // LÓGICA NUEVA: Obtener el ID del usuario activo y asignarlo a la película
        ContextService.getInstance().getItem("usuarioActivo")
                .filter(obj -> obj instanceof Usuario) // Aseguramos que el objeto sea un Usuario
                .map(obj -> ((Usuario) obj).getId())   // Obtenemos el ID del Usuario
                .ifPresentOrElse(
                        // Si el ID existe, lo asignamos.
                        userId -> pelicula.setIdUsuario(userId),
                        // Si no hay usuario activo, mostramos un error (esto no debería pasar
                        // si el menú Añadir está deshabilitado hasta el login).
                        () -> JOptionPane.showMessageDialog(this, "Debe iniciar sesión para añadir una película.", "Error de Sesión", JOptionPane.WARNING_MESSAGE)
                );
        // Si el idUsuario se ha asignado (solo si el Optional no estaba vacío)
        if (pelicula.getIdUsuario() != 0) {
            /* LOGICA ANTIGUA, PROBLEMA: GUARDA 2 VECES EN ARCHIVO
            if(peliculaService.save(pelicula).isEmpty()){
                JOptionPane.showMessageDialog(this, "Error al guardar","Error",JOptionPane.WARNING_MESSAGE);
            } else {
                dispose();
            }

             */
            // 1. Añade a la lista en memoria (esto asigna el ID y añade al List<Pelicula> interno)
            peliculaService.addPelicula(pelicula);
            // 2. Persiste la lista completa, sobrescribiendo el CSV
            peliculaService.saveAll(((CsvPeliculaService)peliculaService).getPeliculas());
            // 3. Ya no necesitas el Optional, solo verificar si se guardó
            JOptionPane.showMessageDialog(this, "Pelicula guardada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
        /* Eliminado esta condicion para que no se guarde dos veces la pelicula en el csv.
        if(peliculaService.save(pelicula).isEmpty()){
            JOptionPane.showMessageDialog(this, "Error al guardar","",JOptionPane.WARNING_MESSAGE);
        } else dispose();

         */

    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
