package application.domain.valueobjects;


public final class WarehouseType extends DomainCatalog {
    public static final WarehouseType MARKETPLACE = new WarehouseType(
            "MARKETPLACE", "Marketplace", "Bodega administrada directamente por NexusMarket para su operación propia.");

    public static final WarehouseType SELLER = new WarehouseType(
            "SELLER", "Seller", "Bodega privada administrada por un vendedor registrado en el marketplace.");

    private WarehouseType(String code, String name, String description) {
        super(code, name, description);
    }
}
