package principal;

/** Describe una columna de una tabla para que PanelCrud arme el formulario automáticamente. */
public class Campo {

    public enum Tipo { TEXTO, ENTERO, DECIMAL, OPCIONES, REFERENCIA }

    public final String columna;
    public final String etiqueta;
    public final Tipo tipo;
    public boolean llave = false;       // es la llave primaria
    public boolean automatico = false;  // la genera la base de datos (AUTOINCREMENT)
    public boolean opcional = false;    // puede quedar vacío (NULL)
    public String[] opciones = new String[0]; // para OPCIONES
    public String sqlReferencia = "";   // para REFERENCIA: SELECT id, texto_a_mostrar FROM ...
    public Double min = null;           // rango permitido (ENTERO / DECIMAL)
    public Double max = null;

    private Campo(String columna, String etiqueta, Tipo tipo) {
        this.columna = columna;
        this.etiqueta = etiqueta;
        this.tipo = tipo;
    }

    public static Campo texto(String columna, String etiqueta) {
        return new Campo(columna, etiqueta, Tipo.TEXTO);
    }

    public static Campo entero(String columna, String etiqueta) {
        return new Campo(columna, etiqueta, Tipo.ENTERO);
    }

    public static Campo decimal(String columna, String etiqueta) {
        return new Campo(columna, etiqueta, Tipo.DECIMAL);
    }

    public static Campo opciones(String columna, String etiqueta, String... opciones) {
        Campo c = new Campo(columna, etiqueta, Tipo.OPCIONES);
        c.opciones = opciones;
        return c;
    }

    public static Campo referencia(String columna, String etiqueta, String sql) {
        Campo c = new Campo(columna, etiqueta, Tipo.REFERENCIA);
        c.sqlReferencia = sql;
        return c;
    }

    public Campo llave() {
        this.llave = true;
        return this;
    }

    public Campo automatico() {
        this.automatico = true;
        this.llave = true;
        return this;
    }

    public Campo opcional() {
        this.opcional = true;
        return this;
    }

    public Campo rango(double min, double max) {
        this.min = min;
        this.max = max;
        return this;
    }
}
