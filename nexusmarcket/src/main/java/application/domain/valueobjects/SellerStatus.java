package application.domain.valueobjects;


public final class SellerStatus extends DomainCatalog {

    public static final SellerStatus UNDER_REVIEW = new SellerStatus(
            "UNDER_REVIEW", "Under Review", "El vendedor fue registrado y está siendo validado por el Administrador.");

    public static final SellerStatus ACTIVE = new SellerStatus(
            "ACTIVE", "Active", "El vendedor está aprobado y puede publicar productos y operar en el marketplace.");

    public static final SellerStatus SUSPENDED = new SellerStatus(
            "SUSPENDED", "Suspended", "El vendedor fue suspendido y no puede publicar ni vender productos.");

    private SellerStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
