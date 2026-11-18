package principal;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Pantalla de bienvenida con el total de registros de cada tabla. */
public class PanelInicio extends JPanel implements Refrescable {

    private static final String[][] TABLAS = {
        {"Productos", "productos"},
        {"Clientes", "clientes"},
        {"Categorías", "categorias"},
        {"Ventas", "ventas"},
        {"Usuarios", "usuarios"}
    };

    private final JLabel[] numeros = new JLabel[TABLAS.length];

    public PanelInicio() {
        setLayout(new BorderLayout(10, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel bienvenida = new JLabel("Bienvenido, " + Sesion.nombre);
        bienvenida.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel rol = new JLabel("Rol: " + Sesion.rol + "   |   Use el menú de arriba para gestionar la información.");
        JPanel norte = new JPanel(new GridLayout(2, 1, 0, 6));
        norte.add(bienvenida);
        norte.add(rol);
        add(norte, BorderLayout.NORTH);

        JPanel tarjetas = new JPanel(new GridLayout(1, TABLAS.length, 15, 15));
        for (int i = 0; i < TABLAS.length; i++) {
            JPanel t = new JPanel(new GridLayout(2, 1));
            t.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 200, 150), 2),
                    BorderFactory.createEmptyBorder(18, 10, 18, 10)));
            numeros[i] = new JLabel("0", SwingConstants.CENTER);
            numeros[i].setFont(new Font("Segoe UI", Font.BOLD, 40));
            numeros[i].setForeground(new Color(0, 150, 110));
            JLabel nombre = new JLabel(TABLAS[i][0], SwingConstants.CENTER);
            nombre.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            t.add(numeros[i]);
            t.add(nombre);
            tarjetas.add(t);
        }
        JPanel centro = new JPanel(new BorderLayout());
        centro.add(tarjetas, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        refrescar();
    }

    @Override
    public void refrescar() {
        try (Connection cn = Conexion.abrir(); Statement st = cn.createStatement()) {
            for (int i = 0; i < TABLAS.length; i++) {
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + TABLAS[i][1])) {
                    if (rs.next()) {
                        numeros[i].setText(String.valueOf(rs.getInt(1)));
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
