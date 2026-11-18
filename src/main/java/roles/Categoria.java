package roles;

public class Categoria {
    
    // 1. Atributos privados para mantener la seguridad
    private String nombre = "Novas";
    private String descripcion = "Son lo más cómodo que probarán tus pies, y con una buena tecnología.";

    // 2. Constructor
    public Categoria() {
    }

    // 3. Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // 4. El método toString() para facilitar la impresión
    @Override
    public String toString() {
        return "Categoría: " + nombre + "\n" +
               "Descripción: " + descripcion;
    }
}