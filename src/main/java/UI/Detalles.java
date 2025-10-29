package UI;

import Model.Pelicula;
import Service.ContextService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class Detalles extends JDialog{
    private JPanel contentPanel;
    private JButton volverButton;
    private JLabel imagen;
    private JLabel labelTitulo;
    private JLabel labelDirector;
    private JLabel labelAño;
    private JLabel labelDescripcion;

    public Detalles(JFrame parent){
        setContentPane(contentPanel);
        setModal(true);
        getRootPane().setDefaultButton(volverButton);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        // Juego juego = AppContext.juegoSeleccionado;
        Pelicula pelicula = (Pelicula) ContextService.getInstance().getItem("peliculaSeleccionada").get();

        //nombre la ventana
        setTitle(pelicula.getTitulo());
        labelTitulo.setText(pelicula.getTitulo());
        labelDescripcion.setText(pelicula.getDescripcion());
        labelAño.setText(String.valueOf((pelicula.getAño())));
        labelDirector.setText(pelicula.getDirector());

        volverButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        // call onCancel() on ESCAPE
        contentPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pack();

    }
    private void onOK() {
        dispose();
    }
}
