package application.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class FiscalData {
    private String fullNameOrCompany;
    private String documentType;
    private String documentNumber;
    private String email;
    private String fiscalAddress;
    private String city;
    private String country;
}
