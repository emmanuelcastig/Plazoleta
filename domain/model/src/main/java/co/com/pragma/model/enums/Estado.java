package co.com.pragma.model.enums;

public enum Estado {
    PENDIENTE("PENDIENTE"),
    EN_PREPARACION("EN_PREPARACION"),
    LISTO("LISTO"),
    ENTREGADO("ENTREGADO");

    private final String nombre;

    Estado
            (String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
