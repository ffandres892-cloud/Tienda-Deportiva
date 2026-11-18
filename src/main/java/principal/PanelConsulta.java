package principal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/** Muestra el resultado de una consulta SQL (con JOIN, GROUP BY, etc.) en una tabla de solo lectura. */
public class PanelConsulta extends JPanel implements Refrescable {

    private final String sql;
    private final DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JLabel lblEstado = new JLabel(" ");

    public PanelConsulta(String titulo, String descripcion, String sql) {
        this.sql = sql;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel lblDesc = new JLabel(descripcion);
        JPanel norte = new JPanel(new GridLayout(2, 1));
        norte.add(lblTitulo);
        norte.add(lblDesc);
        add(norte, BorderLayout.NORTH);

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.setAutoCreateRowSorter(true);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> refrescar());
        JPanel sur = new JPanel(new BorderLayout());
        sur.add(btnActualizar, BorderLayout.WEST);
        sur.add(lblEstado, BorderLayout.EAST);
        add(sur, BorderLayout.SOUTH);

        refrescar();
    }

    @Override
    public void refrescar() {
        try (Connection cn = Conexion.abrir();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            ResultSetMetaData md = rs.getMetaData();
            int n = md.getColumnCount();
            String[] columnas = new String[n];
            for (int i = 0; i < n; i++) {
                columnas[i] = md.getColumnLabel(i + 1);
            }
            modelo.setDataVector(new Object[0][0], columnas);
            while (rs.next()) {
                Object[] fila = new Object[n];
                for (int j = 0; j < n; j++) {
                    fila[j] = rs.getObject(j + 1);
                }
                modelo.addRow(fila);
            }
            lblEstado.setText(modelo.getRowCount() + " fila(s)");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en la consulta", JOptionPane.ERROR_MESSAGE);
        }
    }
}
