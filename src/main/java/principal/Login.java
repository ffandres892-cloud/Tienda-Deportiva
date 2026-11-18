package principal;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Ventana de inicio de sesión. Valida contra la tabla usuarios. */
public class Login extends JFrame {

    private final JTextField txtUsuario = new JTextField(16);
    private final JPasswordField txtClave = new JPasswordField(16);

    public Login() {
        super("RUUCK - Inicio de sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel raiz = new JPanel(new BorderLayout());

        JPanel banner = new JPanel(new BorderLayout(0, 8));
        banner.setBackground(new Color(30, 34, 48));
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(0, 200, 150)),
                BorderFactory.createEmptyBorder(18, 40, 14, 40)));
        ImageIcon logo = Imagenes.conAltura("logo.png", 170);
        JLabel t1 = new JLabel(logo, SwingConstants.CENTER);
        if (logo == null) { // si falta la imagen, se muestra el nombre en texto
            t1.setText("RUUCK - NEXUS EDITION");
            t1.setForeground(Color.WHITE);
            t1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        }
        JLabel t2 = new JLabel("Inicie sesión para continuar", SwingConstants.CENTER);
        t2.setForeground(new Color(0, 230, 200));
        banner.add(t1, BorderLayout.CENTER);
        banner.add(t2, BorderLayout.SOUTH);
        raiz.add(banner, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        g.gridy = 0;
        form.add(new JLabel("Usuario:"), g);
        g.gridx = 1;
        form.add(txtUsuario, g);
        g.gridx = 0;
        g.gridy = 1;
        form.add(new JLabel("Clave:"), g);
        g.gridx = 1;
        form.add(txtClave, g);
        raiz.add(form, BorderLayout.CENTER);

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.addActionListener(e -> ingresar());
        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> System.exit(0));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        botones.add(btnIngresar);
        botones.add(btnSalir);
        raiz.add(botones, BorderLayout.SOUTH);

        setContentPane(raiz);
        Imagenes.iconoVentana(this); // logo como icono de la ventana
        getRootPane().setDefaultButton(btnIngresar); // Enter = Ingresar
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void ingresar() {
        String usuario = txtUsuario.getText().trim();
        String clave = new String(txtClave.getPassword());
        if (usuario.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escriba el usuario y la clave.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sql = "SELECT cedula, nombres, apellidos, nombreusuario, role FROM usuarios WHERE nombreusuario = ? AND clave = ?";
        try (Connection cn = Conexion.abrir(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, clave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Sesion.iniciar(rs.getString("cedula"),
                            rs.getString("nombres") + " " + rs.getString("apellidos"),
                            rs.getString("nombreusuario"),
                            rs.getString("role"));
                    new ventanatienda2().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Usuario o clave incorrectos.", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                    txtClave.setText("");
                    txtClave.requestFocus();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
