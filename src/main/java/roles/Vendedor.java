package roles;

public class Vendedor {
    
    // 1. Variables privadas con los tipos de datos matemáticos correctos
    private String cargo = "Encargado de ventas";
    private double salario = 1300000.0; // Usamos double para manejar dinero
    private double comision = 0.10;     // 0.10 representa el 10%
    private int idVendedor = 1003;      // Formato camelCase
    
    // 2. Constructor (Se ejecuta cuando creas un nuevo Vendedor)
    public Vendedor() {
    }

    // 3. Getters y Setters (Permiten leer y modificar los datos de forma segura)
    
    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public double getComision() {
        return comision;
    }

    public void setComision(double comision) {
        this.comision = comision;
    }

    public int getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(int idVendedor) {
        this.idVendedor = idVendedor;
    }
    
    // 4. Método de prueba: Calcular cuánto gana por una venta
    public double calcularGananciaExtra(double totalVenta) {
        return totalVenta * this.comision;
    }
}