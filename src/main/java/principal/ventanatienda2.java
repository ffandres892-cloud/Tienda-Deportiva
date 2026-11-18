package principal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ander
 */
public class ventanatienda2 extends javax.swing.JFrame {

    // Componentes de la interfaz únicos declarados manualmente para evitar bloqueos
    private javax.swing.JComboBox<String> cmbCliente; // cliente elegido (viene de la base de datos)
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JComboBox<String> cmbModelo;
    private javax.swing.JComboBox<String> cmbTalla;
    private javax.swing.JComboBox<String> cmbColor;
    private javax.swing.JTable tablaProductos;
    private javax.swing.JTextArea txtFacturaFinal;
    private javax.swing.JLabel lblTotal;
    
    private DefaultTableModel modeloTabla;
    private double totalAcumulado = 0.0;
    private JMenuBar barraMenu;
    private JPanel encabezado; // franja superior con el logo (conserva sus colores en ambos temas)

    // Pantallas (se cambian con el menú) y datos leídos de la base de datos
    private final CardLayout cartas = new CardLayout();
    private final JPanel contenido = new JPanel(cartas);
    private final Map<String, JPanel> paneles = new HashMap<>();
    private final Map<String, Object[]> productos = new LinkedHashMap<>(); // modelo -> {id, precio, stock}
    private final List<String> cedulasClientes = new ArrayList<>();         // misma posición que cmbCliente
    // Pon true para mostrar emojis cuando falte el icono PNG (puede verse como cuadritos en algunos equipos)
    private static final boolean USAR_EMOJIS = false;

    public ventanatienda2() {
        super("RUUCK - Nexus Edition Control Center");
        inicializarComponentesManuales();
        configurarTablaInicial();
        cargarDatosVenta();      // modelos y clientes desde la base de datos
        registrarPaneles();      // CRUD y consultas
        barraMenu = crearBarraMenu();
        setJMenuBar(barraMenu); // Barra de menú superior
        aplicarTema(true);      // Arranca en modo oscuro
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(1150, 650);
        setLocationRelativeTo(null); // Centra la ventana
        mostrar("inicio");
    }

    private void configurarTablaInicial() {
        String[] columnas = {"Modelo", "Talla", "Color", "Precio Unit.", "Cant.", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tablaProductos.setModel(modeloTabla);
    }

    private void inicializarComponentesManuales() {
        // Layout principal dividido en dos secciones (Izquierda: Formulario, Derecha: Tabla y Resultados)
        JPanel panelVenta = new JPanel(new BorderLayout(15, 15));
        
        // --- PANEL IZQUIERDO (FORMULARIO) ---
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        panelIzquierdo.setPreferredSize(new Dimension(380, 550));

        JLabel lblTitulo = new JLabel("⚡ CENTRO DE PERSONALIZACIÓN NEXBEAT ⚡");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        cmbCliente = new JComboBox<>();
        cmbCliente.setMaximumSize(new Dimension(360, 30));
        cmbCliente.setAlignmentX(Component.LEFT_ALIGNMENT);

        cmbModelo = new javax.swing.JComboBox<>(); // se llena desde la base de datos
        cmbModelo.setMaximumSize(new Dimension(360, 30));
        cmbModelo.setAlignmentX(Component.LEFT_ALIGNMENT);

        cmbTalla = new javax.swing.JComboBox<>(new String[] { "36 EUR / 5 US", "38 EUR / 6.5 US", "40 EUR / 8 US", "42 EUR / 9.5 US", "44 EUR / 11 US" });
        cmbTalla.setMaximumSize(new Dimension(360, 30));
        cmbTalla.setAlignmentX(Component.LEFT_ALIGNMENT);

        cmbColor = new javax.swing.JComboBox<>(new String[] { "Gris Neón (Nexus Signature)", "Negro Absoluto", "Blanco Estelar", "Rojo Lava", "Azul Eléctrico" });
        cmbColor.setMaximumSize(new Dimension(360, 30));
        cmbColor.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtCantidad = new JTextField();
        txtCantidad.setMaximumSize(new Dimension(360, 30));
        txtCantidad.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnAgregar = new JButton("➕ Añadir Modelo");
        btnAgregar.setBackground(new Color(204, 255, 204));
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAgregar.setMaximumSize(new Dimension(360, 35));
        btnAgregar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAgregar.addActionListener(e -> btnAgregarActionPerformed());

        txtFacturaFinal = new JTextArea(12, 30);
        txtFacturaFinal.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtFacturaFinal.setEditable(false);
        JScrollPane scrollFactura = new JScrollPane(txtFacturaFinal);
        scrollFactura.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnGenerarFactura = new JButton("💾 Emitir Factura Nexbeat");
        btnGenerarFactura.setBackground(new Color(204, 229, 255));
        btnGenerarFactura.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGenerarFactura.setMaximumSize(new Dimension(360, 35));
        btnGenerarFactura.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGenerarFactura.addActionListener(e -> btnGenerarFacturaActionPerformed());

        // Organizar elementos en el panel izquierdo
        panelIzquierdo.add(lblTitulo); panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 15)));
        panelIzquierdo.add(new JLabel("Cliente:")); panelIzquierdo.add(cmbCliente); panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 10)));
        panelIzquierdo.add(new JLabel("Modelo Tecnológico:")); panelIzquierdo.add(cmbModelo); panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 10)));
        panelIzquierdo.add(new JLabel("Talla Asignada:")); panelIzquierdo.add(cmbTalla); panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 10)));
        panelIzquierdo.add(new JLabel("Color de Capa:")); panelIzquierdo.add(cmbColor); panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 10)));
        panelIzquierdo.add(new JLabel("Cantidad (Pares):")); panelIzquierdo.add(txtCantidad); panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 12)));
        panelIzquierdo.add(btnAgregar); panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 15)));
        panelIzquierdo.add(scrollFactura); panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 10)));
        panelIzquierdo.add(btnGenerarFactura);

        // --- PANEL DERECHO (TABLA Y CONTROLES) ---
        JPanel panelDerecho = new JPanel(new BorderLayout(10, 10));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        tablaProductos = new JTable();
        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        
        JPanel panelBotonesTabla = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton btnEditarCantidad = new JButton("📝 Cambiar Unidades");
        JButton btnQuitar = new JButton("❌ Quitar Seleccionado");
        btnQuitar.setBackground(new Color(255, 204, 204));
        
        btnEditarCantidad.addActionListener(e -> btnEditarCantidadActionPerformed());
        btnQuitar.addActionListener(e -> btnQuitarActionPerformed());
        
        panelBotonesTabla.add(btnEditarCantidad);
        panelBotonesTabla.add(btnQuitar);

        lblTotal = new JLabel("TOTAL DE COMPRA: $0.0 COP");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(new Color(0, 102, 51));

        JPanel panelInferiorDerecho = new JPanel(new BorderLayout());
        panelInferiorDerecho.add(panelBotonesTabla, BorderLayout.WEST);
        panelInferiorDerecho.add(lblTotal, BorderLayout.EAST);

        panelDerecho.add(scrollTabla, BorderLayout.CENTER);
        panelDerecho.add(panelInferiorDerecho, BorderLayout.SOUTH);

        // Ensamblar todo al marco JFrame
        panelVenta.add(panelIzquierdo, BorderLayout.WEST);
        panelVenta.add(panelDerecho, BorderLayout.CENTER);
        // Raíz: encabezado con el logo arriba y las pantallas (cartas) debajo
        JPanel raiz = new JPanel(new BorderLayout());
        encabezado = crearEncabezado();
        raiz.add(encabezado, BorderLayout.NORTH);
        raiz.add(contenido, BorderLayout.CENTER);
        setContentPane(raiz);
        Imagenes.iconoVentana(this); // logo como icono de la ventana
        registrar("venta", panelVenta);
    }

    private void btnAgregarActionPerformed() {
        try {
            if (txtCantidad.getText().trim().isEmpty()) {
                txtFacturaFinal.setText("⚠️ Alerta: Ingrese una cantidad válida.");
                return;
            }

            String modelo = cmbModelo.getSelectedItem().toString();
            String talla = cmbTalla.getSelectedItem().toString();
            String color = cmbColor.getSelectedItem().toString();
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());

            if (cantidad <= 0) {
                txtFacturaFinal.setText("⚠️ Alerta: La cantidad debe ser superior a cero.");
                return;
            }

            Object[] datos = productos.get(modelo);
            if (datos == null) {
                txtFacturaFinal.setText("⚠️ Seleccione un modelo válido.");
                return;
            }
            double precioUnitario = (double) datos[1];
            int stock = (int) datos[2];
            int yaEnCarrito = 0;
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                if (modeloTabla.getValueAt(i, 0).equals(modelo)) {
                    yaEnCarrito += (int) modeloTabla.getValueAt(i, 4);
                }
            }
            if (cantidad + yaEnCarrito > stock) {
                txtFacturaFinal.setText("⚠️ Stock insuficiente: quedan " + stock + " par(es) de este modelo.");
                return;
            }

            double subtotal = precioUnitario * cantidad;

            Object[] fila = {modelo, talla, color, precioUnitario, cantidad, subtotal};
            modeloTabla.addRow(fila);

            actualizarGranTotal();
            txtCantidad.setText(""); 
            txtFacturaFinal.setText("✅ Modelo añadido al carrito.");

        } catch (NumberFormatException e) {
            txtFacturaFinal.setText("❌ Error: Use solo números enteros para la cantidad.");
        }
    }

    private void btnQuitarActionPerformed() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada >= 0) {
            modeloTabla.removeRow(filaSeleccionada);
            actualizarGranTotal();
            txtFacturaFinal.setText("🗑️ Producto retirado de la lista actual.");
        } else {
            txtFacturaFinal.setText("⚠️ Por favor, seleccione un elemento de la tabla para quitarlo.");
        }
    }

    private void btnEditarCantidadActionPerformed() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada >= 0) {
            try {
                if (txtCantidad.getText().trim().isEmpty()) {
                    txtFacturaFinal.setText("⚠️ Ingrese el nuevo número en el campo 'Cantidad' antes de editar.");
                    return;
                }
                int nuevaCantidad = Integer.parseInt(txtCantidad.getText().trim());
                if (nuevaCantidad <= 0) return;

                double precioUnit = (double) modeloTabla.getValueAt(filaSeleccionada, 3);
                double nuevoSubtotal = precioUnit * nuevaCantidad;

                modeloTabla.setValueAt(nuevaCantidad, filaSeleccionada, 4);
                modeloTabla.setValueAt(nuevoSubtotal, filaSeleccionada, 5);

                actualizarGranTotal();
                txtCantidad.setText("");
                txtFacturaFinal.setText("📝 Cantidad actualizada correctamente.");
            } catch (NumberFormatException e) {
                txtFacturaFinal.setText("❌ Error de formato en la modificación.");
            }
        } else {
            txtFacturaFinal.setText("⚠️ Seleccione el registro de la tabla que desea modificar.");
        }
    }

    private void btnGenerarFacturaActionPerformed() {
        if (modeloTabla.getRowCount() == 0) {
            txtFacturaFinal.setText("⚠️ El carrito de compras se encuentra vacío.");
            return;
        }

        int idx = cmbCliente.getSelectedIndex();
        String cliente = idx <= 0 ? "Consumidor Final" : String.valueOf(cmbCliente.getSelectedItem());
        String cedula = idx <= 0 ? null : cedulasClientes.get(idx);

        int numero = registrarVenta(cedula); // guarda en la base de datos (ventas, detalle_venta y stock)
        if (numero < 0) {
            return; // registrarVenta ya mostró el motivo
        }

        StringBuilder factura = new StringBuilder();
        factura.append("==================================================\n");
        factura.append("       RUUCK NEXUS EDITION - FACTURA DIGITAL       \n");
        factura.append("==================================================\n");
        factura.append("FACTURA N° ").append(numero).append("      FECHA: ").append(java.time.LocalDate.now()).append("\n");
        factura.append("CLIENTE : ").append(cliente).append("\n");
        if (!Sesion.nombre.isEmpty()) {
            factura.append("VENDEDOR: ").append(Sesion.nombre).append("\n");
        }
        factura.append("--------------------------------------------------\n");
        factura.append(String.format("%-22s %-6s %-10s %-8s\n", "Modelo", "Cant.", "Precio U.", "Subtotal"));
        factura.append("--------------------------------------------------\n");

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String mod = modeloTabla.getValueAt(i, 0).toString();
            if (mod.length() > 20) mod = mod.substring(0, 18) + "..";

            String cant = modeloTabla.getValueAt(i, 4).toString();
            String prU = modeloTabla.getValueAt(i, 3).toString();
            String sub = modeloTabla.getValueAt(i, 5).toString();

            factura.append(String.format("%-22s %-6s $%-9s $%-8s\n", mod, cant, prU, sub));
        }

        factura.append("--------------------------------------------------\n");
        factura.append("TOTAL NETO A PAGAR: $").append(totalAcumulado).append(" COP\n");
        factura.append("==================================================\n");
        factura.append("   Gracias por confiar en el entorno Nexbeat");

        txtFacturaFinal.setText(factura.toString());

        // La venta ya quedó guardada: se vacía el carrito y se actualiza el stock mostrado
        modeloTabla.setRowCount(0);
        actualizarGranTotal();
        cargarDatosVenta();
    }

    /** Guarda la compra en la base de datos (todo o nada). Devuelve el número de venta, o -1 si falló. */
    private int registrarVenta(String cedulaCliente) {
        try (Connection cn = Conexion.abrir()) {
            cn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO ventas (cliente_cedula, vendedor_cedula, total) VALUES (?, ?, ?)")) {
                    if (cedulaCliente == null) {
                        ps.setNull(1, Types.VARCHAR);
                    } else {
                        ps.setString(1, cedulaCliente);
                    }
                    if (Sesion.cedula.isEmpty()) {
                        ps.setNull(2, Types.VARCHAR);
                    } else {
                        ps.setString(2, Sesion.cedula);
                    }
                    ps.setDouble(3, totalAcumulado);
                    ps.executeUpdate();
                }
                int ventaId;
                try (Statement st = cn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
                    rs.next();
                    ventaId = rs.getInt(1);
                }
                try (PreparedStatement det = cn.prepareStatement(
                        "INSERT INTO detalle_venta (venta_id, producto_id, talla, color, cantidad, precio_unitario) VALUES (?, ?, ?, ?, ?, ?)");
                     PreparedStatement stk = cn.prepareStatement(
                        "UPDATE productos SET stock = stock - ? WHERE id = ? AND stock >= ?")) {
                    for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                        String modelo = modeloTabla.getValueAt(i, 0).toString();
                        Object[] datos = productos.get(modelo);
                        if (datos == null) {
                            throw new SQLException("El producto ya no existe: " + modelo);
                        }
                        int productoId = (int) datos[0];
                        int cantidad = (int) modeloTabla.getValueAt(i, 4);

                        det.setInt(1, ventaId);
                        det.setInt(2, productoId);
                        det.setString(3, modeloTabla.getValueAt(i, 1).toString());
                        det.setString(4, modeloTabla.getValueAt(i, 2).toString());
                        det.setInt(5, cantidad);
                        det.setDouble(6, (double) modeloTabla.getValueAt(i, 3));
                        det.executeUpdate();

                        stk.setInt(1, cantidad);
                        stk.setInt(2, productoId);
                        stk.setInt(3, cantidad);
                        if (stk.executeUpdate() == 0) {
                            throw new SQLException("Stock insuficiente para " + modelo);
                        }
                    }
                }
                cn.commit();
                return ventaId;
            } catch (SQLException ex) {
                cn.rollback(); // si algo falla no se guarda nada a medias
                throw ex;
            }
        } catch (SQLException ex) {
            txtFacturaFinal.setText("❌ No se pudo registrar la venta: " + ex.getMessage());
            return -1;
        }
    }

    /** Lee de la base de datos los modelos (con precio y stock) y los clientes. */
    private void cargarDatosVenta() {
        String modeloSel = cmbModelo.getSelectedItem() == null ? null : cmbModelo.getSelectedItem().toString();
        int clienteSel = cmbCliente.getSelectedIndex();
        productos.clear();
        cedulasClientes.clear();
        cmbModelo.removeAllItems();
        cmbCliente.removeAllItems();
        cmbCliente.addItem("Consumidor final");
        cedulasClientes.add(null);
        try (Connection cn = Conexion.abrir(); Statement st = cn.createStatement()) {
            try (ResultSet rs = st.executeQuery("SELECT id, modelo, precio, stock FROM productos ORDER BY id")) {
                while (rs.next()) {
                    productos.put(rs.getString("modelo"),
                            new Object[]{rs.getInt("id"), rs.getDouble("precio"), rs.getInt("stock")});
                    cmbModelo.addItem(rs.getString("modelo"));
                }
            }
            try (ResultSet rs = st.executeQuery("SELECT cedula, nombres || ' ' || apellidos FROM clientes ORDER BY nombres")) {
                while (rs.next()) {
                    cedulasClientes.add(rs.getString(1));
                    cmbCliente.addItem(rs.getString(2) + " (" + rs.getString(1) + ")");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
        if (modeloSel != null) {
            cmbModelo.setSelectedItem(modeloSel);
        }
        if (clienteSel >= 0 && clienteSel < cmbCliente.getItemCount()) {
            cmbCliente.setSelectedIndex(clienteSel);
        }
    }

    // ===================== BARRA DE MENÚ =====================
    private JMenuBar crearBarraMenu() {
        // Colores del tema (se definen antes de crear los items)
        Color verde = new Color(0, 200, 150);
        UIManager.put("Menu.selectionBackground", verde);
        UIManager.put("Menu.selectionForeground", Color.BLACK);
        UIManager.put("MenuItem.selectionBackground", verde);
        UIManager.put("MenuItem.selectionForeground", Color.BLACK);
        UIManager.put("CheckBoxMenuItem.selectionBackground", verde);
        UIManager.put("CheckBoxMenuItem.selectionForeground", Color.BLACK);
        UIManager.put("MenuItem.acceleratorForeground", new Color(125, 125, 135));
        UIManager.put("MenuItem.acceleratorSelectionForeground", Color.BLACK);
        UIManager.put("CheckBoxMenuItem.acceleratorForeground", new Color(125, 125, 135));
        UIManager.put("CheckBoxMenuItem.acceleratorSelectionForeground", Color.BLACK);

        JMenuBar barra = new JMenuBar();
        int ctrl = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        // ---- Menú ARCHIVO ----
        JMenu mArchivo = new JMenu("Archivo");
        conIcono(mArchivo, "archivo.png", "📁");
        mArchivo.setMnemonic(KeyEvent.VK_A);

        JMenuItem iNueva = new JMenuItem("Nueva compra");
        conIcono(iNueva, "nueva.png", "🆕");
        iNueva.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ctrl));
        iNueva.addActionListener(e -> enVenta(this::nuevaCompra));

        JMenuItem iGuardar = new JMenuItem("Guardar factura como .txt...");
        conIcono(iGuardar, "guardar.png", "💾");
        iGuardar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ctrl));
        iGuardar.addActionListener(e -> guardarFacturaEnArchivo());

        JMenuItem iSalir = new JMenuItem("Salir");
        conIcono(iSalir, "salir.png", "🚪");
        iSalir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ctrl));
        iSalir.addActionListener(e -> salirDeLaAplicacion());

        JMenuItem iInicio = itemPanel("Inicio", "inicio", "inicio.png");
        JMenuItem iCerrar = new JMenuItem("Cerrar sesión");
        conIcono(iCerrar, "sesion.png", "");
        iCerrar.addActionListener(e -> cerrarSesion());

        mArchivo.add(iInicio);
        mArchivo.addSeparator();
        mArchivo.add(iNueva);
        mArchivo.add(iGuardar);
        mArchivo.addSeparator();
        mArchivo.add(iCerrar);
        mArchivo.add(iSalir);

        // ---- Menú CARRITO ----
        JMenu mCarrito = new JMenu("Carrito");
        conIcono(mCarrito, "carrito.png", "🛒");
        mCarrito.setMnemonic(KeyEvent.VK_C);

        JMenuItem iAnadir = new JMenuItem("Añadir modelo");
        conIcono(iAnadir, "anadir.png", "➕");
        iAnadir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ctrl));
        iAnadir.addActionListener(e -> enVenta(this::btnAgregarActionPerformed));

        JMenuItem iEditar = new JMenuItem("Cambiar unidades");
        conIcono(iEditar, "editar.png", "📝");
        iEditar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ctrl));
        iEditar.addActionListener(e -> enVenta(this::btnEditarCantidadActionPerformed));

        JMenuItem iQuitar = new JMenuItem("Quitar seleccionado");
        conIcono(iQuitar, "quitar.png", "❌");
        iQuitar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        iQuitar.addActionListener(e -> enVenta(this::btnQuitarActionPerformed));

        JMenuItem iVaciar = new JMenuItem("Vaciar carrito");
        conIcono(iVaciar, "vaciar.png", "🗑️");
        iVaciar.addActionListener(e -> enVenta(this::vaciarCarrito));

        mCarrito.add(iAnadir);
        mCarrito.add(iEditar);
        mCarrito.add(iQuitar);
        mCarrito.addSeparator();
        mCarrito.add(iVaciar);

        // ---- Menú FACTURA ----
        JMenu mFactura = new JMenu("Factura");
        conIcono(mFactura, "factura.png", "🧾");
        mFactura.setMnemonic(KeyEvent.VK_F);

        JMenuItem iEmitir = new JMenuItem("Emitir factura");
        conIcono(iEmitir, "emitir.png", "⚡");
        iEmitir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ctrl));
        iEmitir.addActionListener(e -> enVenta(this::btnGenerarFacturaActionPerformed));

        JMenuItem iLimpiar = new JMenuItem("Limpiar pantalla de factura");
        conIcono(iLimpiar, "limpiar.png", "🧹");
        iLimpiar.addActionListener(e -> enVenta(() -> txtFacturaFinal.setText("")));

        mFactura.add(iEmitir);
        mFactura.add(iLimpiar);

        // ---- Menú GESTIÓN (un CRUD por tabla) ----
        JMenu mGestion = new JMenu("Gestión");
        mGestion.setMnemonic(KeyEvent.VK_G);
        conIcono(mGestion, "gestion.png", "");
        mGestion.add(itemPanel("Productos", "productos", "productos.png"));
        mGestion.add(itemPanel("Clientes", "clientes", "clientes.png"));
        mGestion.add(itemPanel("Categorías", "categorias", "categorias.png"));
        if (Sesion.esAdmin()) {
            mGestion.addSeparator();
            mGestion.add(itemPanel("Usuarios", "usuarios", "usuarios.png"));
        }

        // ---- Menú CONSULTAS ----
        JMenu mConsultas = new JMenu("Consultas");
        mConsultas.setMnemonic(KeyEvent.VK_N);
        conIcono(mConsultas, "consultas.png", "");
        mConsultas.add(itemPanel("Historial de ventas", "c_ventas", "ventas.png"));
        mConsultas.add(itemPanel("Detalle de ventas", "c_detalle", "factura.png"));
        mConsultas.add(itemPanel("Productos más vendidos", "c_vendidos", "catalogo.png"));
        mConsultas.add(itemPanel("Inventario por categoría", "c_inventario", "productos.png"));

        // ---- Menú VER ----
        JMenu mVer = new JMenu("Ver");
        conIcono(mVer, "ver.png", "👁");
        mVer.setMnemonic(KeyEvent.VK_V);

        JMenuItem iCatalogo = new JMenuItem("Catálogo de precios");
        conIcono(iCatalogo, "catalogo.png", "📋");
        iCatalogo.addActionListener(e -> mostrarCatalogo());

        JCheckBoxMenuItem iOscuro = new JCheckBoxMenuItem("Modo oscuro");
        conIcono(iOscuro, "oscuro.png", "🌙");
        iOscuro.setSelected(true); // arranca en modo oscuro
        iOscuro.addActionListener(e -> aplicarTema(iOscuro.isSelected()));

        mVer.add(iCatalogo);
        mVer.addSeparator();
        mVer.add(iOscuro);

        // ---- Menú AYUDA ----
        JMenu mAyuda = new JMenu("Ayuda");
        conIcono(mAyuda, "ayuda.png", "❓");
        mAyuda.setMnemonic(KeyEvent.VK_Y);

        JMenuItem iComoUsar = new JMenuItem("¿Cómo usar?");
        conIcono(iComoUsar, "comousar.png", "💡");
        iComoUsar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        iComoUsar.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "1. Escribe el nombre del cliente.\n" +
            "2. Elige modelo, talla y color.\n" +
            "3. Escribe la cantidad y pulsa 'Añadir modelo'.\n" +
            "4. Para cambiar unidades: selecciona una fila, escribe la nueva\n" +
            "    cantidad y usa Carrito > Cambiar unidades.\n" +
            "5. Pulsa 'Emitir factura' para ver el resumen.\n" +
            "6. Guárdala con Archivo > Guardar factura.",
            "Cómo usar", JOptionPane.INFORMATION_MESSAGE));

        JMenuItem iAcerca = new JMenuItem("Acerca de");
        conIcono(iAcerca, "acerca.png", "ℹ️");
        iAcerca.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "RUUCK - Nexus Edition\nControl Center v1.0\nEntorno Nexbeat",
            "Acerca de", JOptionPane.INFORMATION_MESSAGE));

        mAyuda.add(iComoUsar);
        mAyuda.addSeparator();
        mAyuda.add(iAcerca);

        barra.add(mArchivo);
        barra.add(mCarrito);
        barra.add(mFactura);
        barra.add(mGestion);
        barra.add(mConsultas);
        barra.add(mVer);
        barra.add(mAyuda);
        estilizarMenu(barra, true);
        return barra;
    }

    // Pone un icono PNG (carpeta src/main/resources/iconos). Si el archivo no existe, usa el emoji.
    private void conIcono(AbstractButton boton, String archivo, String emoji) {
        ImageIcon ic = icono(archivo);
        if (ic != null) {
            boton.setIcon(ic);
            boton.setIconTextGap(8);
        } else if (USAR_EMOJIS) {
            boton.setText(emoji + " " + boton.getText());
        }
    }

    private ImageIcon icono(String nombre) {
        java.net.URL url = getClass().getResource("/iconos/" + nombre);
        if (url == null) return null;
        Image img = new ImageIcon(url).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    // Estilo visual del menú según el tema (oscuro o claro)
    private void estilizarMenu(JMenuBar barra, boolean oscuro) {
        Color panel = UIManager.getColor("Panel.background");
        Color fondoBarra = oscuro ? new Color(40, 40, 45) : panel;   // mismo color que la ventana
        Color fondoPopup = fondoBarra;
        Color letra = oscuro ? new Color(235, 235, 235) : new Color(40, 40, 40);
        Color separador = oscuro ? new Color(75, 75, 85) : new Color(190, 190, 190);
        Color verde = new Color(0, 200, 150);

        barra.setOpaque(true);
        barra.setBackground(fondoBarra);
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, verde));

        for (int i = 0; i < barra.getMenuCount(); i++) {
            JMenu menu = barra.getMenu(i);
            menu.setFont(new Font("Segoe UI", Font.BOLD, 13));
            menu.setForeground(letra);
            menu.setOpaque(true);
            menu.setBackground(fondoBarra);
            menu.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

            menu.getPopupMenu().setBackground(fondoPopup);
            menu.getPopupMenu().setBorder(BorderFactory.createLineBorder(verde, 1));

            for (Component c : menu.getMenuComponents()) {
                if (c instanceof JMenuItem) {
                    JMenuItem item = (JMenuItem) c;
                    item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    item.setOpaque(true);
                    item.setBackground(fondoPopup);
                    item.setForeground(letra);
                    item.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
                } else if (c instanceof JSeparator) {
                    c.setBackground(fondoPopup);
                    c.setForeground(separador);
                }
            }
        }
    }

    // ===================== ENCABEZADO CON LOGO =====================
    private JPanel crearEncabezado() {
        JPanel h = new JPanel(new BorderLayout(14, 0));
        h.setBackground(new Color(30, 34, 48));
        h.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(0, 200, 150)),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));

        JLabel lblLogo = new JLabel();
        ImageIcon logo = Imagenes.conAltura("logo_encabezado.png", 56);
        if (logo != null) {
            lblLogo.setIcon(logo);
        }

        JLabel titulo = new JLabel("RUUCK  NEXUS EDITION");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel lema = new JLabel("Tu estilo, en cada paso");
        lema.setForeground(new Color(0, 230, 200));
        lema.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(lema);

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        izquierda.setOpaque(false);
        izquierda.add(lblLogo);
        izquierda.add(textos);

        JLabel usuario = new JLabel(Sesion.nombre.isEmpty() ? "" : Sesion.nombre + "   |   " + Sesion.rol);
        usuario.setForeground(new Color(0, 230, 200));
        usuario.setFont(new Font("Segoe UI", Font.BOLD, 13));

        h.add(izquierda, BorderLayout.WEST);
        h.add(usuario, BorderLayout.EAST);
        return h;
    }

    // ===================== PANTALLAS (CRUD y consultas) =====================
    private void registrar(String clave, JPanel panel) {
        paneles.put(clave, panel);
        contenido.add(panel, clave);
    }

    private void registrarPaneles() {
        boolean admin = Sesion.esAdmin();

        registrar("inicio", new PanelInicio());

        // CRUD de Productos (los zapatos): solo el administrador modifica
        registrar("productos", new PanelCrud("Productos", "productos", List.of(
                Campo.entero("id", "ID").automatico(),
                Campo.texto("modelo", "Modelo"),
                Campo.referencia("categoria_id", "Categoría",
                        "SELECT id, nombre FROM categorias ORDER BY nombre").opcional(),
                Campo.decimal("precio", "Precio (COP)").rango(1, 100000000),
                Campo.entero("stock", "Stock").rango(0, 100000)
        ), admin));

        registrar("clientes", new PanelCrud("Clientes", "clientes", List.of(
                Campo.texto("cedula", "Cédula").llave(),
                Campo.texto("nombres", "Nombres"),
                Campo.texto("apellidos", "Apellidos"),
                Campo.texto("telefono", "Teléfono").opcional(),
                Campo.texto("correo", "Correo").opcional()
        ), true));

        registrar("categorias", new PanelCrud("Categorías", "categorias", List.of(
                Campo.entero("id", "ID").automatico(),
                Campo.texto("nombre", "Nombre")
        ), admin));

        if (admin) {
            registrar("usuarios", new PanelCrud("Usuarios del sistema", "usuarios", List.of(
                    Campo.texto("cedula", "Cédula").llave(),
                    Campo.texto("nombres", "Nombres"),
                    Campo.texto("apellidos", "Apellidos"),
                    Campo.texto("nombreusuario", "Usuario"),
                    Campo.texto("clave", "Clave"),
                    Campo.opciones("role", "Rol", "Administrador", "Vendedor")
            ), true));
        }

        registrar("c_ventas", new PanelConsulta("Historial de ventas",
                "Cada venta con su cliente y vendedor (LEFT JOIN: aparecen aunque no tengan cliente).",
                """
                SELECT v.id AS "N° venta",
                       v.fecha AS "Fecha",
                       COALESCE(c.nombres || ' ' || c.apellidos, 'Consumidor final') AS "Cliente",
                       COALESCE(u.nombres || ' ' || u.apellidos, '(sin vendedor)') AS "Vendedor",
                       v.total AS "Total"
                FROM ventas v
                LEFT JOIN clientes c ON c.cedula = v.cliente_cedula
                LEFT JOIN usuarios u ON u.cedula = v.vendedor_cedula
                ORDER BY v.id DESC
                """));

        registrar("c_detalle", new PanelConsulta("Detalle de ventas",
                "Las líneas de cada venta: modelo, talla, color, cantidad y subtotal.",
                """
                SELECT d.venta_id AS "N° venta",
                       p.modelo AS "Modelo",
                       d.talla AS "Talla",
                       d.color AS "Color",
                       d.cantidad AS "Cantidad",
                       d.precio_unitario AS "Precio unit.",
                       d.cantidad * d.precio_unitario AS "Subtotal"
                FROM detalle_venta d
                JOIN productos p ON p.id = d.producto_id
                ORDER BY d.venta_id DESC, d.id
                """));

        registrar("c_vendidos", new PanelConsulta("Productos más vendidos",
                "Pares vendidos y total facturado por modelo (GROUP BY).",
                """
                SELECT p.modelo AS "Modelo",
                       COALESCE(SUM(d.cantidad), 0) AS "Pares vendidos",
                       COALESCE(SUM(d.cantidad * d.precio_unitario), 0) AS "Total vendido"
                FROM productos p
                LEFT JOIN detalle_venta d ON d.producto_id = p.id
                GROUP BY p.id
                ORDER BY "Pares vendidos" DESC, p.modelo
                """));

        registrar("c_inventario", new PanelConsulta("Inventario por categoría",
                "Cantidad de modelos y stock total de cada categoría.",
                """
                SELECT c.nombre AS "Categoría",
                       COUNT(p.id) AS "Modelos",
                       COALESCE(SUM(p.stock), 0) AS "Stock total"
                FROM categorias c
                LEFT JOIN productos p ON p.categoria_id = c.id
                GROUP BY c.id
                ORDER BY c.nombre
                """));
    }

    private void mostrar(String clave) {
        JPanel p = paneles.get(clave);
        if (p == null) {
            return;
        }
        if ("venta".equals(clave)) {
            cargarDatosVenta(); // por si cambiaron productos o clientes
        }
        if (p instanceof Refrescable) {
            ((Refrescable) p).refrescar(); // vuelve a leer la base de datos al entrar
        }
        cartas.show(contenido, clave);
    }

    /** Muestra la pantalla de venta y luego ejecuta la acción (para los atajos del menú Carrito/Factura). */
    private void enVenta(Runnable accion) {
        mostrar("venta");
        accion.run();
    }

    private JMenuItem itemPanel(String texto, String clave, String archivoIcono) {
        JMenuItem item = new JMenuItem(texto);
        conIcono(item, archivoIcono, "");
        item.addActionListener(e -> mostrar(clave));
        return item;
    }

    private void cerrarSesion() {
        Sesion.cerrar();
        dispose();
        new Login().setVisible(true);
    }

    private void nuevaCompra() {
        int r = JOptionPane.showConfirmDialog(this,
            "¿Iniciar una nueva compra? Se borrará el carrito actual.",
            "Nueva compra", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            modeloTabla.setRowCount(0);
            cmbCliente.setSelectedIndex(0);
            txtCantidad.setText("");
            txtFacturaFinal.setText("");
            actualizarGranTotal();
        }
    }

    private void vaciarCarrito() {
        if (modeloTabla.getRowCount() == 0) {
            txtFacturaFinal.setText("⚠️ El carrito ya está vacío.");
            return;
        }
        int r = JOptionPane.showConfirmDialog(this,
            "¿Quitar todos los productos del carrito?",
            "Vaciar carrito", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            modeloTabla.setRowCount(0);
            actualizarGranTotal();
            txtFacturaFinal.setText("🗑️ Carrito vaciado.");
        }
    }

    private void guardarFacturaEnArchivo() {
        String texto = txtFacturaFinal.getText();
        if (texto.trim().isEmpty() || !texto.contains("FACTURA DIGITAL")) {
            JOptionPane.showMessageDialog(this,
                "Primero emite una factura (menú Factura > Emitir factura).",
                "Nada que guardar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new File("factura_ruuck.txt"));
        if (selector.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(selector.getSelectedFile(), java.nio.charset.StandardCharsets.UTF_8)) {
                fw.write(texto);
                JOptionPane.showMessageDialog(this, "Factura guardada correctamente.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo guardar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void salirDeLaAplicacion() {
        int r = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas salir?",
            "Salir", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void mostrarCatalogo() {
        DefaultTableModel m = new DefaultTableModel(new String[]{"Modelo", "Precio (COP)", "Stock"}, 0);
        try (Connection cn = Conexion.abrir(); Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT modelo, precio, stock FROM productos ORDER BY id")) {
            while (rs.next()) {
                m.addRow(new Object[]{rs.getString(1), rs.getDouble(2), rs.getInt(3)});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JTable t = new JTable(m);
        t.setEnabled(false);
        t.getColumnModel().getColumn(0).setPreferredWidth(300);
        JScrollPane sp = new JScrollPane(t);
        sp.setPreferredSize(new Dimension(520, 160));
        JOptionPane.showMessageDialog(this, sp, "Catálogo de precios", JOptionPane.PLAIN_MESSAGE);
    }

    private void aplicarTema(boolean oscuro) {
        Color fondo = oscuro ? new Color(40, 40, 45) : null;
        Color texto = oscuro ? new Color(230, 230, 230) : null;
        aplicarColores(getContentPane(), fondo, texto);
        if (!oscuro) {
            // Al volver al modo claro, se reinstalan los colores por defecto
            SwingUtilities.updateComponentTreeUI(this);
        }
        lblTotal.setForeground(oscuro ? new Color(0, 230, 160) : new Color(0, 102, 51));
        estilizarMenu(barraMenu, oscuro);
        repaint();
    }

    private void aplicarColores(Component c, Color fondo, Color texto) {
        if (c == encabezado) {
            return; // el encabezado con el logo siempre conserva sus colores
        }
        // Los botones de colores y la etiqueta de total conservan su estilo propio
        if (!(c instanceof JButton) && c != lblTotal) {
            c.setBackground(fondo);
            c.setForeground(texto);
        }
        // El cursor de escritura debe verse sobre fondo oscuro
        if (c instanceof javax.swing.text.JTextComponent) {
            ((javax.swing.text.JTextComponent) c).setCaretColor(texto != null ? texto : Color.BLACK);
        }
        if (c instanceof Container) {
            for (Component hijo : ((Container) c).getComponents()) {
                aplicarColores(hijo, fondo, texto);
            }
        }
    }

    private void actualizarGranTotal() {
        totalAcumulado = 0.0;
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            totalAcumulado += (double) modeloTabla.getValueAt(i, 5);
        }
        lblTotal.setText("TOTAL DE COMPRA: $" + totalAcumulado + " COP");
    }

    public static void main(String args[]) {
        Login.main(args); // primero el inicio de sesión
    }
}