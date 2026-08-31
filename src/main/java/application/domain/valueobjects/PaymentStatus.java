package application.domain.valueobjects;

public final class PaymentStatus extends DomainCatalog {

    public static final PaymentStatus PENDING = new PaymentStatus(
            "PENDING", "Pending", "La transacción fue iniciada y está pendiente de validación por el motor interno.");

    public static final PaymentStatus APPROVED = new PaymentStatus(
            "APPROVED", "Approved", "La transacción fue validada y aprobada. El pedido avanza a preparación.");

    public static final PaymentStatus REJECTED = new PaymentStatus(
            "REJECTED", "Rejected", "La transacción fue rechazada por el motor de validación interno (fondos insuficientes, datos inválidos, etc.).");

    public static final PaymentStatus REFUNDED = new PaymentStatus(
            "REFUNDED", "Refunded", "El monto de la transacción fue reembolsado al comprador por cancelación o devolución aprobada.");

    private PaymentStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
