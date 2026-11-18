package roles;

public class Pago {
    
    // 1. Atributos privados con tipos de datos correctos (monto ahora es double)
    private String metodoPago = "Efectivo"; // Corregido el texto y asignado un método individual
    private double monto = 350000.0;        // Listo para operaciones financieras

    // 2. Constructor vacío
    public Pago() {
    }

    // 3. Getters y Setters para proteger los datos
    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    // 4. Método toString() listo para tu clase Imprimir
    @Override
    public String toString() {
        return "Método de Pago: " + metodoPago + "\n" +
               "Monto: $" + monto;
    }
}