package application.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class DeliveryAddress {

    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String additionalInstructions;
    private String recipientName;
    private String recipientPhone;
}
