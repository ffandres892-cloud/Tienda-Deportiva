package roles;

public class Inventario {
    
    // 1. Atributos privados en formato camelCase
    private int cantidadDisponible = 100;
    private String ubicacion = "Calle 5b sur #24-56";

    // 2. Constructor vacío
    public Inventario() {
    }

    // 3. Getters y Setters para proteger los datos
    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    // 4. Método toString() listo para tu clase Imprimir
    @Override
    public String toString() {
        return "Cantidad Disponible: " + cantidadDisponible + "\n" +
               "Ubicación: " + ubicacion;
    }
}