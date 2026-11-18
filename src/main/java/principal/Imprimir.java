package principal; // 1. Cambiamos el paquete al paquete raíz

// 2. Importamos todas las clases del paquete roles para poder usarlas aquí
import roles.*; 

public class Imprimir {

    public static void main(String[] args) {
        // Instanciamos los objetos (ahora Java los reconoce gracias al import roles.*)
        Cliente cliente = new Cliente();
        Empleado empleado = new Empleado();
        Vendedor vendedor = new Vendedor();
        Administrador admin = new Administrador();
        ZapatoDeportivo zapato = new ZapatoDeportivo();
        Categoria categoria = new Categoria();
        Inventario inventario = new Inventario();
        Venta venta = new Venta();
        DetalleVenta detalle = new DetalleVenta();
        Pago pago = new Pago();
        Factura factura = new Factura();

        // Imprimimos usando los métodos toString() de cada clase
        System.out.println("=== ROLES ===");
        System.out.println("--- Datos del Cliente ---");
        System.out.println(cliente.toString());
        System.out.println();

        System.out.println("--- Datos del Empleado ---");
        System.out.println(empleado.toString());
        System.out.println();

        System.out.println("--- Datos del Vendedor ---");
        System.out.println(vendedor.toString());
        System.out.println();

        System.out.println("--- Datos del Administrador ---");
        System.out.println(admin.toString());
        System.out.println();

        System.out.println("=== PRODUCTOS ===");
        System.out.println("--- Ficha de Zapato Deportivo ---");
        System.out.println(zapato.toString());
        System.out.println();

        System.out.println("--- Categoría ---");
        System.out.println(categoria.toString());
        System.out.println();

        System.out.println("--- Inventario ---");
        System.out.println(inventario.toString());
        System.out.println();

        System.out.println("=== PROCESO DE VENTA ===");
        System.out.println("--- Registro de Venta ---");
        System.out.println(venta.toString());
        System.out.println();

        System.out.println("--- Detalles de la Venta ---");
        System.out.println(detalle.toString());
        System.out.println();

        System.out.println("--- Información de Pago ---");
        System.out.println(pago.toString());
        System.out.println();

        System.out.println("--- Factura Generada ---");
        System.out.println(factura.toString());
    }
}