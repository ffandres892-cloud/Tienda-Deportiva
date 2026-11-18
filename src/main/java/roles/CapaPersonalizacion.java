package roles;

public class CapaPersonalizacion {
    private static CapaPersonalizacion instancia;
    private String sistema;
    private String vibra;

    private CapaPersonalizacion() {
        this.sistema = "RUUCK - Nexus Edition";
        this.vibra = "Alto Rendimiento";
    }

    public static CapaPersonalizacion getInstancia() {
        if (instancia == null) {
            instancia = new CapaPersonalizacion();
        }
        return instancia;
    }

    public void iniciar() {
        System.out.println("=========================================");
        System.out.println(" ⚡ ARRANCANDO ENTORNO NEXBEAT ⚡");
        System.out.println("=========================================");
        System.out.println(" -> Sistema: " + sistema);
        System.out.println(" -> Modo: " + vibra);
        System.out.println("=========================================\n");
    }

    // ¡CORREGIDO! Ahora este método sí funciona y no arroja error
    public void inicializarEntorno() {
        iniciar(); 
    }
}