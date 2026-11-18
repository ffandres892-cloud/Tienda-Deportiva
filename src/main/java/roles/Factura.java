package roles;

public class Factura {
    
    // 1. Atributos privados en formato camelCase
    private int numeroFactura = 10000;
    private String fechaEmision = "14/04/05"; // Nota: ¡Excelente formato para la fecha!

    // 2. Constructor
    public Factura() {
    }

    // 3. Getters y Setters para mayor seguridad
    public int getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(int numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    // 4. Método toString() listo para tu clase Imprimir
    @Override
    public String toString() {
        return "Número de Factura: " + numeroFactura + "\n" +
               "Fecha de Emisión: " + fechaEmision;
    }
}