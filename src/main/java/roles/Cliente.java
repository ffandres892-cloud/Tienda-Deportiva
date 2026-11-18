package roles;

public class Cliente {
    
    // 1. Atributos privados para seguridad (Teléfono ahora es String)
    private int idCliente = 1095939456;
    private String nombre = "Emiliano Herrera";
    private String telefono = "311255672"; // Protegido contra errores de números grandes
    private String email = "emilianoher@gmail.com";
    private String direccion = "Calle 3a sur #34-56";

    // 2. Constructor
    public Cliente() {
    }

    // 3. Getters y Setters
    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    // 4. Método toString() para tu clase Imprimir
    @Override
    public String toString() {
        return "ID Cliente: " + idCliente + "\n" +
               "Nombre: " + nombre + "\n" +
               "Teléfono: " + telefono + "\n" +
               "Email: " + email + "\n" +
               "Dirección: " + direccion;
    }
}