package co.com.pragma.model.enums;

public enum Estado {
    PENDIENTE("PENDIENTE"),
    EN_PROCESO("EN_PROCESO"),
    LISTO("LISTO");

    private final String nombre;

    Estado
            (String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
