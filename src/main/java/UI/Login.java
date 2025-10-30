package UI;

import Model.Usuario;
import Service.ContextService;
import Service.UsuarioService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JDialog {
    private JPanel contentPane;
    private JButton buttonOk;
    private JButton buttonCancel;
    private JTextField txtUser;
    private JPasswordField txtPassword;

    UsuarioService usuarioService;
    JFrame parent;

    public Login(JFrame parent, UsuarioService us) {
        // para poder usar makeLogin
        this.parent = parent;

        //  Inyección de dependencia
        usuarioService = us;

        setContentPane(contentPane);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);
        getRootPane().setDefaultButton(buttonOk);
        pack();
        this.setLocationRelativeTo(parent);

        buttonOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
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
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        usuarioService.validate(txtUser.getText(), txtPassword.getText()).ifPresentOrElse(
                (Usuario user) -> {
                    //AppContext.usuarioActivo = user;
                    ContextService.getInstance().addItem("usuarioActivo", user);
                    dispose();
                },
                () -> {
                    JOptionPane.showMessageDialog(parent, "Usuario no existe");
                });
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}