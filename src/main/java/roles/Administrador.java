package roles;

public class Administrador {
    
    // 1. Atributos privados y tipos de datos correctos
    private String permisos = "Se asignan según lo que se necesite";
    private int idAdministrador = 1004;
    private double salario = 1800000.0; // Ahora es un número matemático
    private String cargo = "Administrador";
    private String nombre = "Manuel Fernandez";

    // 2. Constructor
    public Administrador() {
    }

    // 3. Getters y Setters para seguridad
    public String getPermisos() {
        return permisos;
    }

    public void setPermisos(String permisos) {
        this.permisos = permisos;
    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // 4. ¡El truco de magia! El método toString()
    @Override
    public String toString() {
        return "ID Administrador: " + idAdministrador + "\n" +
               "Nombre: " + nombre + "\n" +
               "Cargo: " + cargo + "\n" +
               "Salario: $" + salario + "\n" +
               "Permisos: " + permisos;
    }
}