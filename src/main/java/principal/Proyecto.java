package principal; // p minúscula igual que la ventana

import roles.CapaPersonalizacion;

public class Proyecto {

    public static void main(String[] args) {
        try {
            // Inicializar entorno Nexbeat
            CapaPersonalizacion nexbeatVibe = CapaPersonalizacion.getInstancia();
            nexbeatVibe.iniciar();

            // Primero el inicio de sesión; al ingresar se abre la ventana de la tienda
            Login.main(args);

        } catch (Exception e) {
            System.out.println("❌ Error al levantar la interfaz gráfica:");
            e.printStackTrace();
        }
    }
}
