package application.domain.valueobjects;


public final class WarehouseStatus extends DomainCatalog {

    public static final WarehouseStatus ACTIVE = new WarehouseStatus(
            "ACTIVE", "Active", "La bodega está operativa y puede recibir o despachar inventario.");

    public static final WarehouseStatus CLOSED = new WarehouseStatus(
            "CLOSED", "Closed", "La bodega fue cerrada permanentemente y no puede operar.");

    private WarehouseStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
