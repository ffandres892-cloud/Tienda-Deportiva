package principal;

/** Guarda quién inició sesión y con qué rol. */
public final class Sesion {

    public static String cedula = "";
    public static String nombre = "";
    public static String usuario = "";
    public static String rol = "";

    private Sesion() { }

    public static void iniciar(String cedula, String nombre, String usuario, String rol) {
        Sesion.cedula = cedula;
        Sesion.nombre = nombre;
        Sesion.usuario = usuario;
        Sesion.rol = rol;
    }

    public static void cerrar() {
        iniciar("", "", "", "");
    }

    public static boolean esAdmin() {
        return "Administrador".equals(rol);
    }

    public static boolean esVendedor() {
        return "Vendedor".equals(rol);
    }
}
