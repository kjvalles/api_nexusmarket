package application.domain.models;

import application.domain.valueobjects.Address;
import application.domain.valueobjects.WarehouseStatus;
import application.domain.valueobjects.WarehouseType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Warehouse {

    private String id;
    private String name;
    private Address address;
    private String vendorId;
    private WarehouseType type;
    private WarehouseStatus status;
    private String description;
    private int maxCapacity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
