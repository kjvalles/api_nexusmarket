package application.domain.models;

import application.domain.valueobjects.SellerStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Seller extends User {

    private String businessName;
    private String taxId;
    private List<String> warehouseIds;
    private SellerStatus sellerStatus;
    private String businessDescription;
    private double rating;
    private LocalDateTime incorporationDate;
    private int activeProductsCount;
}
