package principal;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.Image;
import java.net.URL;

/** Carga las imágenes de la carpeta src/main/resources/imagenes (logo de la tienda). */
public final class Imagenes {

    private Imagenes() { }

    /** Devuelve la imagen, o null si el archivo no existe. */
    public static Image imagen(String nombre) {
        URL url = Imagenes.class.getResource("/imagenes/" + nombre);
        return url == null ? null : new ImageIcon(url).getImage();
    }

    /** Devuelve la imagen escalada a la altura indicada (mantiene la proporción), o null si no existe. */
    public static ImageIcon conAltura(String nombre, int altura) {
        Image img = imagen(nombre);
        if (img == null) {
            return null;
        }
        int ancho = Math.max(1, img.getWidth(null) * altura / Math.max(1, img.getHeight(null)));
        return new ImageIcon(img.getScaledInstance(ancho, altura, Image.SCALE_SMOOTH));
    }

    /** Pone el logo como icono de la ventana (barra de título y barra de tareas). */
    public static void iconoVentana(JFrame ventana) {
        Image logo = imagen("logo.png");
        if (logo != null) {
            ventana.setIconImage(logo);
        }
    }
}
