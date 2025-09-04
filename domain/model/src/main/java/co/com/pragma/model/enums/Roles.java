package co.com.pragma.model.enums;

public enum Roles {
    EMPLEADO("EMPLEADO"),
    CLIENTE("CLIENTE");

    private final String nombre;

    Roles(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

}
