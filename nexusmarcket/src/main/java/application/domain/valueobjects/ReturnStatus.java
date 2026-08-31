package application.domain.valueobjects;


public final class ReturnStatus extends DomainCatalog {

    public static final ReturnStatus REQUESTED = new ReturnStatus(
            "REQUESTED", "Requested",
            "El comprador solicitó formalmente la devolución del producto.");

    public static final ReturnStatus IN_TRANSIT_RETURN = new ReturnStatus(
            "IN_TRANSIT_RETURN", "In Transit - Return",
            "El producto está en tránsito de vuelta a la bodega para su inspección.");

    public static final ReturnStatus RECEIVED_AT_WAREHOUSE = new ReturnStatus(
            "RECEIVED_AT_WAREHOUSE", "Received at Warehouse",
            "La bodega recibió el producto devuelto. Está pendiente de inspección para aprobar o rechazar la devolución.");
            
    public static final ReturnStatus APPROVED = new ReturnStatus(
            "APPROVED", "Approved",
            "La devolución fue aprobada tras inspección. Se genera un Reembolso y se reingresa el producto al inventario.");
    public static final ReturnStatus REJECTED_DUE_TO_DAMAGE = new ReturnStatus(
            "REJECTED_DUE_TO_DAMAGE", "Rejected due to Damage",
            "La devolución fue rechazada. El producto llegó con daños imputables al comprador; no se genera reembolso.");

    private ReturnStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
