package principal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD genérico: tabla con los registros + formulario + botones Nuevo / Guardar / Actualizar / Eliminar.
 * Se configura con la lista de campos (Campo) de la tabla.
 */
public class PanelCrud extends JPanel implements Refrescable {

    /** Elemento de un combo: guarda el valor real (id) y el texto que se muestra. */
    private static class Item {
        final String id;
        final String texto;

        Item(String id, String texto) {
            this.id = id;
            this.texto = texto;
        }

        @Override
        public String toString() {
            return texto;
        }
    }

    private final String tabla;
    private final List<Campo> campos;
    private final boolean puedeEditar;
    private final JComponent[] editores;
    private final DefaultTableModel modelo;
    private final JTable jtabla;
    private final JTextField txtBuscar = new JTextField(18);
    private final JLabel lblEstado = new JLabel(" ");
    private final int idxLlave;
    private Object llaveSeleccionada = null; // null = se está creando un registro nuevo

    public PanelCrud(String titulo, String tabla, List<Campo> campos, boolean puedeEditar) {
        this.tabla = tabla;
        this.campos = campos;
        this.puedeEditar = puedeEditar;

        int pk = 0;
        for (int i = 0; i < campos.size(); i++) {
            if (campos.get(i).llave) {
                pk = i;
            }
        }
        this.idxLlave = pk;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        // ---- Parte de arriba: título y búsqueda ----
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnTodo = new JButton("Mostrar todo");
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        barra.add(new JLabel("Buscar:"));
        barra.add(txtBuscar);
        barra.add(btnBuscar);
        barra.add(btnTodo);
        JPanel norte = new JPanel(new BorderLayout());
        norte.add(lblTitulo, BorderLayout.WEST);
        norte.add(barra, BorderLayout.EAST);
        add(norte, BorderLayout.NORTH);

        // ---- Centro: tabla ----
        String[] columnas = new String[campos.size()];
        for (int i = 0; i < columnas.length; i++) {
            columnas[i] = campos.get(i).etiqueta;
        }
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        jtabla = new JTable(modelo);
        jtabla.setRowHeight(24);
        jtabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jtabla.setAutoCreateRowSorter(true);
        jtabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                filaSeleccionada();
            }
        });
        add(new JScrollPane(jtabla), BorderLayout.CENTER);

        // ---- Abajo: formulario y botones ----
        JPanel form = new JPanel(new GridBagLayout());
        editores = new JComponent[campos.size()];
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < campos.size(); i++) {
            Campo c = campos.get(i);
            editores[i] = crearEditor(c);
            g.gridx = (i % 2) * 2;
            g.gridy = i / 2;
            g.weightx = 0;
            g.fill = GridBagConstraints.NONE;
            form.add(new JLabel(c.etiqueta + ":"), g);
            g.gridx = g.gridx + 1;
            g.weightx = 1;
            g.fill = GridBagConstraints.HORIZONTAL;
            form.add(editores[i], g);
        }

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        if (puedeEditar) {
            botones.add(btnNuevo);
            botones.add(btnGuardar);
            botones.add(btnActualizar);
            botones.add(btnEliminar);
        } else {
            botones.add(new JLabel("Modo solo lectura (su rol no permite modificar esta tabla)"));
        }

        JPanel sur = new JPanel(new BorderLayout());
        sur.add(form, BorderLayout.CENTER);
        JPanel pie = new JPanel(new BorderLayout());
        pie.add(botones, BorderLayout.WEST);
        pie.add(lblEstado, BorderLayout.EAST);
        sur.add(pie, BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);

        // ---- Acciones ----
        btnBuscar.addActionListener(e -> cargar());
        txtBuscar.addActionListener(e -> cargar());
        btnTodo.addActionListener(e -> {
            txtBuscar.setText("");
            cargar();
        });
        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());

        refrescar();
    }

    // =====================================================================
    //  Editores del formulario
    // =====================================================================
    private JComponent crearEditor(Campo c) {
        switch (c.tipo) {
            case OPCIONES:
                return new JComboBox<String>(c.opciones);
            case REFERENCIA:
                return new JComboBox<Item>();
            default:
                return new JTextField(16);
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarReferencia(JComboBox<?> combo, Campo c) {
        JComboBox<Item> cb = (JComboBox<Item>) combo;
        cb.removeAllItems();
        if (c.opcional) {
            cb.addItem(new Item(null, "(ninguno)"));
        }
        try (Connection cn = Conexion.abrir();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(c.sqlReferencia)) {
            while (rs.next()) {
                cb.addItem(new Item(rs.getString(1), rs.getString(2)));
            }
        } catch (SQLException ex) {
            error(ex);
        }
    }

    /** Valor que el usuario tiene escrito o elegido en el campo i (null si está vacío). */
    private String texto(int i) {
        JComponent e = editores[i];
        if (e instanceof JTextField) {
            return ((JTextField) e).getText().trim();
        }
        Object o = ((JComboBox<?>) e).getSelectedItem();
        if (o instanceof Item) {
            return ((Item) o).id;
        }
        return o == null ? null : o.toString();
    }

    private void ponerValor(int i, Object valor) {
        String s = valor == null ? null : String.valueOf(valor);
        JComponent e = editores[i];
        if (e instanceof JTextField) {
            ((JTextField) e).setText(s == null ? "" : s);
        } else if (campos.get(i).tipo == Campo.Tipo.OPCIONES) {
            ((JComboBox<?>) e).setSelectedItem(s);
        } else {
            JComboBox<?> cb = (JComboBox<?>) e;
            boolean encontrado = false;
            for (int k = 0; k < cb.getItemCount(); k++) {
                Item it = (Item) cb.getItemAt(k);
                if ((it.id == null && s == null) || (it.id != null && it.id.equals(s))) {
                    cb.setSelectedIndex(k);
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado && cb.getItemCount() > 0) {
                cb.setSelectedIndex(0);
            }
        }
    }

    // =====================================================================
    //  Estado del formulario
    // =====================================================================
    private void limpiar() {
        llaveSeleccionada = null;
        jtabla.clearSelection();
        for (int i = 0; i < campos.size(); i++) {
            Campo c = campos.get(i);
            JComponent e = editores[i];
            if (e instanceof JTextField) {
                JTextField t = (JTextField) e;
                t.setText(c.automatico ? "(automático)" : "");
                t.setEnabled(puedeEditar && !c.automatico);
            } else {
                JComboBox<?> cb = (JComboBox<?>) e;
                if (cb.getItemCount() > 0) {
                    cb.setSelectedIndex(0);
                }
                cb.setEnabled(puedeEditar);
            }
        }
    }

    private void filaSeleccionada() {
        int vista = jtabla.getSelectedRow();
        if (vista < 0) {
            return;
        }
        int fila = jtabla.convertRowIndexToModel(vista);
        for (int i = 0; i < campos.size(); i++) {
            ponerValor(i, modelo.getValueAt(fila, i));
        }
        llaveSeleccionada = modelo.getValueAt(fila, idxLlave);
        for (int i = 0; i < campos.size(); i++) {
            Campo c = campos.get(i);
            // La llave no se puede cambiar al modificar un registro existente
            editores[i].setEnabled(puedeEditar && !c.llave);
        }
    }

    // =====================================================================
    //  Lectura (R del CRUD)
    // =====================================================================
    @Override
    public void refrescar() {
        for (int i = 0; i < campos.size(); i++) {
            if (campos.get(i).tipo == Campo.Tipo.REFERENCIA) {
                cargarReferencia((JComboBox<?>) editores[i], campos.get(i));
            }
        }
        limpiar();
        cargar();
    }

    private void cargar() {
        String filtro = txtBuscar.getText().trim();
        StringBuilder sql = new StringBuilder("SELECT ");
        for (int i = 0; i < campos.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(campos.get(i).columna);
        }
        sql.append(" FROM ").append(tabla);
        boolean hayFiltro = !filtro.isEmpty();
        if (hayFiltro) {
            sql.append(" WHERE ");
            for (int i = 0; i < campos.size(); i++) {
                if (i > 0) {
                    sql.append(" OR ");
                }
                sql.append("CAST(").append(campos.get(i).columna).append(" AS TEXT) LIKE ?");
            }
        }
        sql.append(" ORDER BY ").append(campos.get(idxLlave).columna);

        try (Connection cn = Conexion.abrir();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            if (hayFiltro) {
                for (int i = 0; i < campos.size(); i++) {
                    ps.setString(i + 1, "%" + filtro + "%");
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                modelo.setRowCount(0);
                while (rs.next()) {
                    Object[] fila = new Object[campos.size()];
                    for (int j = 0; j < fila.length; j++) {
                        fila[j] = rs.getObject(j + 1);
                    }
                    modelo.addRow(fila);
                }
            }
            lblEstado.setText(modelo.getRowCount() + " registro(s)");
        } catch (SQLException ex) {
            error(ex);
        }
    }

    // =====================================================================
    //  Crear / Actualizar / Eliminar (C, U, D del CRUD)
    // =====================================================================
    private void guardar() {
        Object[] v = leerValores();
        if (v == null) {
            return;
        }
        StringBuilder cols = new StringBuilder();
        StringBuilder marcas = new StringBuilder();
        List<Object> params = new ArrayList<>();
        for (int i = 0; i < campos.size(); i++) {
            if (campos.get(i).automatico) {
                continue;
            }
            if (cols.length() > 0) {
                cols.append(", ");
                marcas.append(", ");
            }
            cols.append(campos.get(i).columna);
            marcas.append("?");
            params.add(v[i]);
        }
        ejecutar("INSERT INTO " + tabla + " (" + cols + ") VALUES (" + marcas + ")",
                params, "Registro guardado.");
    }

    private void actualizar() {
        if (llaveSeleccionada == null) {
            aviso("Seleccione un registro de la tabla para poder actualizarlo.");
            return;
        }
        Object[] v = leerValores();
        if (v == null) {
            return;
        }
        StringBuilder sets = new StringBuilder();
        List<Object> params = new ArrayList<>();
        for (int i = 0; i < campos.size(); i++) {
            if (campos.get(i).llave) {
                continue;
            }
            if (sets.length() > 0) {
                sets.append(", ");
            }
            sets.append(campos.get(i).columna).append(" = ?");
            params.add(v[i]);
        }
        params.add(llaveSeleccionada);
        ejecutar("UPDATE " + tabla + " SET " + sets + " WHERE " + campos.get(idxLlave).columna + " = ?",
                params, "Registro actualizado.");
    }

    private void eliminar() {
        if (llaveSeleccionada == null) {
            aviso("Seleccione un registro de la tabla para poder eliminarlo.");
            return;
        }
        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el registro " + llaveSeleccionada + "?\nEsta acción no se puede deshacer.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }
        List<Object> params = new ArrayList<>();
        params.add(llaveSeleccionada);
        ejecutar("DELETE FROM " + tabla + " WHERE " + campos.get(idxLlave).columna + " = ?",
                params, "Registro eliminado.");
    }

    /** Valida lo escrito en el formulario y lo convierte al tipo de cada columna. Devuelve null si hay un error. */
    private Object[] leerValores() {
        Object[] v = new Object[campos.size()];
        for (int i = 0; i < campos.size(); i++) {
            Campo c = campos.get(i);
            if (c.automatico) {
                v[i] = null;
                continue;
            }
            String t = texto(i);
            if (t == null || t.isEmpty()) {
                if (c.opcional) {
                    v[i] = null;
                    continue;
                }
                aviso("El campo '" + c.etiqueta + "' es obligatorio.");
                editores[i].requestFocus();
                return null;
            }
            try {
                switch (c.tipo) {
                    case ENTERO: {
                        int n = Integer.parseInt(t);
                        if (fueraDeRango(c, n)) {
                            editores[i].requestFocus();
                            return null;
                        }
                        v[i] = n;
                        break;
                    }
                    case DECIMAL: {
                        double d = Double.parseDouble(t.replace(',', '.'));
                        if (fueraDeRango(c, d)) {
                            editores[i].requestFocus();
                            return null;
                        }
                        v[i] = d;
                        break;
                    }
                    default:
                        v[i] = t;
                }
            } catch (NumberFormatException ex) {
                aviso("El campo '" + c.etiqueta + "' debe ser un número"
                        + (c.tipo == Campo.Tipo.ENTERO ? " entero." : "."));
                editores[i].requestFocus();
                return null;
            }
        }
        return v;
    }

    private boolean fueraDeRango(Campo c, double valor) {
        if ((c.min != null && valor < c.min) || (c.max != null && valor > c.max)) {
            aviso("'" + c.etiqueta + "' debe estar entre " + formato(c.min) + " y " + formato(c.max) + ".");
            return true;
        }
        return false;
    }

    private String formato(Double d) {
        if (d == null) {
            return "-";
        }
        return d == Math.rint(d) ? String.valueOf(d.intValue()) : String.valueOf(d);
    }

    private void ejecutar(String sql, List<Object> params, String mensajeOk) {
        try (Connection cn = Conexion.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p == null) {
                    ps.setNull(i + 1, Types.NULL);
                } else {
                    ps.setObject(i + 1, p);
                }
            }
            ps.executeUpdate();
            limpiar();
            cargar();
            lblEstado.setText(mensajeOk + "  (" + modelo.getRowCount() + " registro(s))");
        } catch (SQLException ex) {
            error(ex);
        }
    }

    // =====================================================================
    //  Mensajes
    // =====================================================================
    private void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    private void error(SQLException ex) {
        String m = ex.getMessage() == null ? "" : ex.getMessage();
        String txt;
        if (m.contains("UNIQUE") || m.contains("PRIMARY KEY")) {
            txt = "Ya existe un registro con esa llave o con un valor que debe ser único.";
        } else if (m.contains("CHECK")) {
            txt = "Alguno de los valores no está permitido. Revise las opciones válidas.";
        } else if (m.contains("FOREIGN KEY")) {
            txt = "El registro está relacionado con otros datos y no se puede completar la operación.";
        } else if (m.contains("NOT NULL")) {
            txt = "Falta llenar un campo obligatorio.";
        } else {
            txt = m;
        }
        JOptionPane.showMessageDialog(this, txt, "No se pudo completar", JOptionPane.ERROR_MESSAGE);
    }
}
