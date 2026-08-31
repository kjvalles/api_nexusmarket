package application.domain.valueobjects;


public final class ShipmentStatus extends DomainCatalog {

    public static final ShipmentStatus PENDING_PACKING = new ShipmentStatus(
            "PENDING_PACKING", "Pending Packing",
            "El envío fue creado y está pendiente de alistamiento y empaque en bodega.");

    public static final ShipmentStatus PACKED = new ShipmentStatus(
            "PACKED", "Packed",
            "El pedido fue empacado correctamente en bodega y está listo para su despacho.");

    public static final ShipmentStatus IN_TRANSIT = new ShipmentStatus(
            "IN_TRANSIT", "In Transit",
            "El envío fue despachado desde la bodega y está en tránsito hacia el destino.");

    public static final ShipmentStatus DELIVERED = new ShipmentStatus(
            "DELIVERED", "Delivered",
            "El envío fue entregado exitosamente al destinatario en la dirección indicada.");

    public static final ShipmentStatus FAILED = new ShipmentStatus(
            "FAILED", "Failed",
            "El intento de entrega falló. Causas posibles: dirección incorrecta, nadie en casa, zona inaccesible.");

    private ShipmentStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
