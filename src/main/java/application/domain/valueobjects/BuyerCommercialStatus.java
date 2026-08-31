package application.domain.valueobjects;
public final class BuyerCommercialStatus extends DomainCatalog {

    public static final BuyerCommercialStatus ENABLED = new BuyerCommercialStatus(
            "ENABLED", "Enabled", "El comprador puede realizar compras sin restricciones.");

    public static final BuyerCommercialStatus SUSPENDED = new BuyerCommercialStatus(
            "SUSPENDED", "Suspended", "El comprador tiene restringida su capacidad de compra temporalmente.");

    private BuyerCommercialStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
