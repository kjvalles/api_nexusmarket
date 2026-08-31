package application.domain.valueobjects;

public final class RefundStatus extends DomainCatalog {

    public static final RefundStatus PENDING = new RefundStatus(
            "PENDING", "Pending",
            "El reembolso fue solicitado y está pendiente de aprobación.");

    public static final RefundStatus APPROVED = new RefundStatus(
            "APPROVED", "Approved",
            "El reembolso fue aprobado y está en proceso de ejecución hacia el comprador.");

    public static final RefundStatus EXECUTED = new RefundStatus(
            "EXECUTED", "Executed",
            "El monto fue reembolsado efectivamente al comprador. Si el método fue VIRTUAL_BALANCE, se acreditó al saldo.");

    private RefundStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
