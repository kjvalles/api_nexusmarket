package application.domain.valueobjects;

public final class PaymentMethod extends DomainCatalog {

    public static final PaymentMethod VIRTUAL_BALANCE = new PaymentMethod(
            "VIRTUAL_BALANCE", "Virtual Balance",
            "Pago descontado del saldo virtual acreditado en la cuenta del comprador en NexusMarket.");

    public static final PaymentMethod MOCK_CREDIT_CARD = new PaymentMethod(
            "MOCK_CREDIT_CARD", "Credit Card (Mock)",
            "Simulación de pago con tarjeta de crédito. Validación interna sin integración con pasarelas externas.");

    public static final PaymentMethod MOCK_TRANSFER = new PaymentMethod(
            "MOCK_TRANSFER", "Bank Transfer (Mock)",
            "Simulación de transferencia bancaria. Validación interna sin integración con entidades bancarias externas.");

    private PaymentMethod(String code, String name, String description) {
        super(code, name, description);
    }
}
