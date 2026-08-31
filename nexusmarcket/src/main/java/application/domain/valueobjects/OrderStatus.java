package application.domain.valueobjects;

public final class OrderStatus extends DomainCatalog {

    public static final OrderStatus PENDING_PAYMENT = new OrderStatus(
            "PENDING_PAYMENT", "Pending Payment", "El pedido está pendiente de confirmación de pago.");

    public static final OrderStatus PAID = new OrderStatus(
            "PAID", "Paid", "El pago fue confirmado y el pedido inicia su proceso de preparación en bodega.");

    public static final OrderStatus PREPARATION = new OrderStatus(
            "PREPARATION", "In Preparation", "El pedido está siendo alistado y empacado en la bodega de origen.");

    public static final OrderStatus DISPATCHED = new OrderStatus(
            "DISPATCHED", "Dispatched", "El pedido fue despachado desde la bodega y está en camino al comprador.");

    public static final OrderStatus DELIVERED = new OrderStatus(
            "DELIVERED", "Delivered", "El pedido fue entregado y recibido por el comprador.");

    public static final OrderStatus COMPLETED = new OrderStatus(
            "COMPLETED", "Completed", "El pedido fue finalizado y el ciclo comercial cerrado satisfactoriamente.");

    public static final OrderStatus CANCELLED = new OrderStatus(
            "CANCELLED", "Cancelled", "El pedido fue cancelado y no será procesado. Aplica reembolso si el pago ya fue procesado.");

    private OrderStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
