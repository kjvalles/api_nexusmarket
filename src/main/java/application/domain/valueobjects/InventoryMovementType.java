package application.domain.valueobjects;

public final class InventoryMovementType extends DomainCatalog {

    public static final InventoryMovementType MANUAL_ENTRY = new InventoryMovementType(
            "MANUAL_ENTRY", "Manual Entry",
            "Ingreso manual de unidades a la bodega. Usado en carga inicial o reposición de stock.");

    public static final InventoryMovementType PURCHASE_RESERVATION = new InventoryMovementType(
            "PURCHASE_RESERVATION", "Purchase Reservation",
            "Reserva de unidades asociada a un pedido confirmado. Reduce el stock vendible sin hacer salida física.");

    public static final InventoryMovementType SALE_EXIT = new InventoryMovementType(
            "SALE_EXIT", "Sale Exit",
            "Salida definitiva de unidades por despacho de un pedido. Descuenta de la reserva y del total disponible.");

    public static final InventoryMovementType LOSS_ADJUSTMENT = new InventoryMovementType(
            "LOSS_ADJUSTMENT", "Loss Adjustment",
            "Ajuste negativo por pérdida, daño, merma o discrepancia encontrada en conteo físico.");

    public static final InventoryMovementType RESERVATION_RELEASE = new InventoryMovementType(
            "RESERVATION_RELEASE", "Reservation Release",
            "Liberación de unidades previamente reservadas por cancelación del pedido antes de su despacho.");
    public static final InventoryMovementType RETURN_ENTRY = new InventoryMovementType(
            "RETURN_ENTRY", "Return Entry",
            "Reingreso de unidades al inventario de bodega por devolución física aprobada y validada.");

    private InventoryMovementType(String code, String name, String description) {
        super(code, name, description);
    }
}
