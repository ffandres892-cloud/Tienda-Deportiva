package roles;

public class Venta {
    
    // 1. Atributos privados con tipos de datos correctos (total ahora es double)
    private int idVenta = 145672;
    private String fecha = "14/04/05";
    private double total = 350000.0; // El resultado de tu operación (250000 + 100000)

    // 2. Constructor vacío
    public Venta() {
    }

    // 3. Getters y Setters para mayor seguridad
    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // 4. Método toString() listo para tu clase Imprimir
    @Override
    public String toString() {
        return "ID de Venta: " + idVenta + "\n" +
               "Fecha de Compra: " + fecha + "\n" +
               "Total de Compra: $" + total;
    }
}