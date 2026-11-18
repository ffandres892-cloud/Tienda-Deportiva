package roles;

public class ZapatoDeportivo { // Nombre corregido a CamelCase
    
    // 1. Atributos privados con tipos de datos correctos (Precio ahora es double)
    private int idProducto = 13245;
    private String marca = "RUUCK";
    private String modelo = "Deportivos";
    private String talla = "De 5 US a 11 US";
    private String color = "Grises, Negros, Blancos, etc...";
    private double precio = 125000.0; // Cambiado a un valor numérico real para cálculos
    private int stock = 100;

    // 2. Constructor vacío
    public ZapatoDeportivo() {
    }

    // 3. Getters y Setters para proteger los datos
    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    // 4. Método toString() listo para tu clase Imprimir
    @Override
    public String toString() {
        return "ID Producto: " + idProducto + "\n" +
               "Marca: " + marca + "\n" +
               "Modelo: " + modelo + "\n" +
               "Tallas: " + talla + "\n" +
               "Colores: " + color + "\n" +
               "Precio: $" + precio + "\n" +
               "Stock Disponible: " + stock;
    }
}