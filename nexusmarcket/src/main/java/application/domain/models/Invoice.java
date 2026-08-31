package application.domain.models;

import application.domain.valueobjects.FiscalData;
import application.domain.valueobjects.InvoiceStatus;
import application.domain.valueobjects.TaxBreakdown;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Invoice {

    private Long id;
    private Long orderId;
    private Long paymentTransactionId;
    private String invoiceNumber;
    private FiscalData buyerFiscalData;
    private TaxBreakdown taxBreakdown;
    private InvoiceStatus status;
    private LocalDateTime issuedAt;
}
