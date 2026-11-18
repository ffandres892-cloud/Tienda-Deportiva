package roles;

public class DetalleVenta { // Cambiado a 'public' para que se pueda usar desde otras clases
    
    // 1. Atributos privados (Encapsulamiento)
    private int cantidad = 2;
    private double subtotal = 350000.0; // Usamos double para manejar dinero de forma matemática

    // 2. Constructor vacío obligatorio
    public DetalleVenta() {
    }

    // 3. Getters y Setters para leer y modificar los datos de forma segura
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    // 4. Método toString() para tu clase Imprimir
    @Override
    public String toString() {
        return "Cantidad Productos: " + cantidad + "\n" +
               "Subtotal: $" + subtotal;
    }
}