package application.domain.valueobjects;

public final class InvoiceStatus extends DomainCatalog {

    public static final InvoiceStatus ISSUED = new InvoiceStatus(
            "ISSUED", "Issued", "La factura fue emitida correctamente tras la confirmación del pago.");

    public static final InvoiceStatus VOIDED = new InvoiceStatus(
            "VOIDED", "Voided", "La factura fue anulada por cancelación del pedido o por procesamiento de un reembolso total.");

    private InvoiceStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
