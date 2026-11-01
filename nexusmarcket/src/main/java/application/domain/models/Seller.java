package application.domain.models;

import application.domain.enums.UserRole;
import application.domain.valueobjects.SellerStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de dominio que representa a un Vendedor registrado por el Administrador.
 */
@Getter
@Setter
public class Seller extends User {

    private String businessName;
    private String taxId;
    private List<String> warehouseIds = new ArrayList<>();
    private SellerStatus sellerStatus;
    private String businessDescription;
    private double rating = 5.0;
    private LocalDateTime incorporationDate;
    private int activeProductsCount = 0;

    public Seller() {
        setRole(UserRole.SELLER);
        this.incorporationDate = LocalDateTime.now();
    }

    public void addWarehouseId(String warehouseId) {
        if (warehouseId != null && !warehouseId.isBlank()) {
            if (this.warehouseIds == null) {
                this.warehouseIds = new ArrayList<>();
            }
            if (!this.warehouseIds.contains(warehouseId)) {
                this.warehouseIds.add(warehouseId);
            }
        }
    }

    public boolean isApproved() {
        return sellerStatus != null && "ACTIVE".equalsIgnoreCase(sellerStatus.getCode());
    }
}
