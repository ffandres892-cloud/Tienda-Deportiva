package roles;

public class Empleado {
    
    // 1. Atributos privados y tipos de datos correctos
    private int idEmpleado = 1001;
    private String nombre = "Jose Diaz";
    private String cargo = "Empleado";
    private double salario = 1300000.0; // Ahora listo para cálculos matemáticos

    // 2. Constructor
    public Empleado() {
    }

    // 3. Getters y Setters para proteger la información
    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

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

    // 4. Método toString() para simplificar tu clase Imprimir
    @Override
    public String toString() {
        return "ID Empleado: " + idEmpleado + "\n" +
               "Nombre: " + nombre + "\n" +
               "Cargo: " + cargo + "\n" +
               "Salario: $" + salario;
    }
}