package application.domain.models;

import application.domain.valueobjects.Address;
import application.domain.valueobjects.BuyerCommercialStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Buyer extends User {

    private Address primaryAddress;
    private List<Address> additionalAddresses;
    private BuyerCommercialStatus commercialStatus;
    private BigDecimal virtualBalance;
    private int completedOrdersCount;
    private double rating;
}
