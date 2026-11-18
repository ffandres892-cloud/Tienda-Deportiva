package principal;

import javax.swing.JFileChooser;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Abre conexiones a la base de datos SQLite tienda.db. */
public final class Conexion {

    private static String ruta = null;

    private Conexion() { }

    public static Connection abrir() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Falta el driver sqlite-jdbc. Revise la dependencia en el pom.xml y haga Clean and Build.");
        }
        if (ruta == null) {
            ruta = buscarRuta();
        }
        Connection cn = DriverManager.getConnection("jdbc:sqlite:" + ruta);
        try (Statement st = cn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON"); // SQLite exige activar las llaves foráneas en cada conexión
        }
        return cn;
    }

    /** Busca tienda.db en la carpeta del proyecto; si no está, pide al usuario que la seleccione. */
    private static String buscarRuta() throws SQLException {
        File f = new File("tienda.db");
        if (f.exists()) {
            return f.getAbsolutePath();
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("No se encontró tienda.db - selecciónela");
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile().getAbsolutePath();
        }
        throw new SQLException("No se encontró el archivo tienda.db");
    }
}
