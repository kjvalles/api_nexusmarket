package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.CreateUserRequestDto;
import application.adapters.rest.dtos.requests.CreateWarehouseRequestDto;
import application.adapters.rest.dtos.requests.RegisterSellerRequestDto;
import application.adapters.rest.dtos.responses.AddressDto;
import application.adapters.rest.dtos.responses.SellerResponseDto;
import application.adapters.rest.dtos.responses.UserResponseDto;
import application.adapters.rest.dtos.responses.WarehouseResponseDto;
import application.domain.models.DefaultUser;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.WarehouseStatus;
import application.domain.valueobjects.WarehouseType;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class AdminRestMapper {

    private AdminRestMapper() {}

    public static User toDomain(CreateUserRequestDto dto) {
        if (dto == null) return null;
        DefaultUser user = new DefaultUser();
        user.setIdentityDocument(dto.getIdentityDocument());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        return user;
    }

    public static Seller toDomain(RegisterSellerRequestDto dto) {
        if (dto == null) return null;
        Seller seller = new Seller();
        seller.setIdentityDocument(dto.getIdentityDocument());
        seller.setFullName(dto.getFullName());
        seller.setEmail(dto.getEmail());
        seller.setBusinessName(dto.getBusinessName());
        seller.setTaxId(dto.getTaxId());
        seller.setBusinessDescription(dto.getBusinessDescription());
        return seller;
    }

    public static Warehouse toDomain(CreateWarehouseRequestDto dto) {
        if (dto == null) return null;
        Warehouse warehouse = new Warehouse();
        warehouse.setId(dto.getId() != null && !dto.getId().isBlank() ? dto.getId() : UUID.randomUUID().toString());
        warehouse.setName(dto.getName());
        warehouse.setVendorId(dto.getVendorId());
        warehouse.setType(resolveWarehouseType(dto.getType()));
        warehouse.setStatus(WarehouseStatus.ACTIVE);
        warehouse.setDescription(dto.getDescription());
        warehouse.setMaxCapacity(dto.getMaxCapacity() != null ? dto.getMaxCapacity() : 1000);
        warehouse.setCreatedAt(LocalDateTime.now());
        warehouse.setUpdatedAt(LocalDateTime.now());

        Address address = new Address(
                dto.getStreet(),
                dto.getCity(),
                dto.getState(),
                dto.getPostalCode(),
                dto.getCountry(),
                dto.getAdditionalInstructions()
        );
        warehouse.setAddress(address);
        return warehouse;
    }

    public static SellerResponseDto toResponseDto(Seller seller) {
        if (seller == null) return null;
        return SellerResponseDto.builder()
                .id(seller.getId())
                .identityDocument(seller.getIdentityDocument())
                .fullName(seller.getFullName())
                .email(seller.getEmail())
                .role(seller.getRole() != null ? seller.getRole().name() : null)
                .status(seller.getStatus() != null ? seller.getStatus().getCode() : null)
                .businessName(seller.getBusinessName())
                .taxId(seller.getTaxId())
                .sellerStatus(seller.getSellerStatus() != null ? seller.getSellerStatus().getCode() : null)
                .businessDescription(seller.getBusinessDescription())
                .rating(seller.getRating())
                .incorporationDate(seller.getIncorporationDate())
                .activeProductsCount(seller.getActiveProductsCount())
                .warehouseIds(seller.getWarehouseIds())
                .createdAt(seller.getCreatedAt())
                .updatedAt(seller.getUpdatedAt())
                .build();
    }

    public static List<SellerResponseDto> toSellerResponseDtoList(List<Seller> sellers) {
        if (sellers == null) return Collections.emptyList();
        return sellers.stream().map(AdminRestMapper::toResponseDto).collect(Collectors.toList());
    }

    public static WarehouseResponseDto toResponseDto(Warehouse warehouse) {
        if (warehouse == null) return null;
        AddressDto addressDto = null;
        if (warehouse.getAddress() != null) {
            addressDto = AddressDto.builder()
                    .street(warehouse.getAddress().getStreet())
                    .city(warehouse.getAddress().getCity())
                    .state(warehouse.getAddress().getState())
                    .postalCode(warehouse.getAddress().getPostalCode())
                    .country(warehouse.getAddress().getCountry())
                    .additionalInstructions(warehouse.getAddress().getAdditionalInstructions())
                    .build();
        }

        return WarehouseResponseDto.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .vendorId(warehouse.getVendorId())
                .type(warehouse.getType() != null ? warehouse.getType().getCode() : null)
                .status(warehouse.getStatus() != null ? warehouse.getStatus().getCode() : null)
                .description(warehouse.getDescription())
                .maxCapacity(warehouse.getMaxCapacity())
                .address(addressDto)
                .createdAt(warehouse.getCreatedAt())
                .updatedAt(warehouse.getUpdatedAt())
                .build();
    }

    public static List<WarehouseResponseDto> toWarehouseResponseDtoList(List<Warehouse> warehouses) {
        if (warehouses == null) return Collections.emptyList();
        return warehouses.stream().map(AdminRestMapper::toResponseDto).collect(Collectors.toList());
    }

    public static UserResponseDto toResponseDto(User user) {
        if (user == null) return null;
        return UserResponseDto.builder()
                .id(user.getId())
                .identityDocument(user.getIdentityDocument())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .status(user.getStatus() != null ? user.getStatus().getCode() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static List<UserResponseDto> toUserResponseDtoList(List<User> users) {
        if (users == null) return Collections.emptyList();
        return users.stream().map(AdminRestMapper::toResponseDto).collect(Collectors.toList());
    }

    private static WarehouseType resolveWarehouseType(String code) {
        if (code != null && "SELLER".equalsIgnoreCase(code)) {
            return WarehouseType.SELLER;
        }
        return WarehouseType.MARKETPLACE;
    }
}
