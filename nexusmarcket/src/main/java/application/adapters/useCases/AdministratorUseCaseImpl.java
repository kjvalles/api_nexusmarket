package application.adapters.useCases;

import application.domain.enums.UserRole;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidUserStatusException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.AdministratorPort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Caso de uso para operaciones del rol Administrador (ADMINISTRATOR).
 */
@Service
@RequiredArgsConstructor
public class AdministratorUseCaseImpl implements AdministratorPort {

    private final UserRepositoryPort userRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final PasswordServicePort passwordServicePort;

    @Override
    public User registerUser(String adminId, User user, String initialPassword) {
        if (user == null) {
            throw new IllegalArgumentException("La información del usuario no puede ser nula.");
        }
        if (initialPassword == null || initialPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña inicial no puede estar vacía.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("El correo electrónico del usuario es obligatorio.");
        }
        if (user.getIdentityDocument() == null || user.getIdentityDocument().isBlank()) {
            throw new IllegalArgumentException("El documento de identidad es obligatorio.");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("El rol del usuario es obligatorio.");
        }

        if (userRepositoryPort.existsByEmail(user.getEmail())) {
            throw new DomainException("El correo electrónico ya se encuentra registrado: " + user.getEmail());
        }
        if (userRepositoryPort.existsByIdentityDocument(user.getIdentityDocument())) {
            throw new DomainException("El documento de identidad ya se encuentra registrado: " + user.getIdentityDocument());
        }

        user.setPassword(passwordServicePort.encode(initialPassword));
        user.setStatus(UserStatus.ACTIVE);
        user.setAuthTokenVersion(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepositoryPort.save(user);
    }

    @Override
    public User getUserById(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("El identificador del usuario no puede estar vacío.");
        }
        return userRepositoryPort.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario", userId));
    }

    @Override
    public Seller registerSeller(String adminId, Seller seller, String initialPassword) {
        if (seller == null) {
            throw new IllegalArgumentException("La información del vendedor no puede ser nula.");
        }
        if (initialPassword == null || initialPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña inicial no puede estar vacía.");
        }
        if (seller.getEmail() == null || seller.getEmail().isBlank()) {
            throw new IllegalArgumentException("El correo electrónico del vendedor es obligatorio.");
        }
        if (seller.getIdentityDocument() == null || seller.getIdentityDocument().isBlank()) {
            throw new IllegalArgumentException("El documento de identidad es obligatorio.");
        }

        if (userRepositoryPort.existsByEmail(seller.getEmail())) {
            throw new DomainException("El correo electrónico ya se encuentra registrado: " + seller.getEmail());
        }
        if (userRepositoryPort.existsByIdentityDocument(seller.getIdentityDocument())) {
            throw new DomainException("El documento de identidad ya se encuentra registrado: " + seller.getIdentityDocument());
        }

        seller.setPassword(passwordServicePort.encode(initialPassword));
        seller.setRole(UserRole.SELLER);
        seller.setStatus(UserStatus.ACTIVE);
        seller.setAuthTokenVersion(1);
        seller.setCreatedAt(LocalDateTime.now());
        seller.setUpdatedAt(LocalDateTime.now());

        Seller savedSeller = sellerRepositoryPort.save(seller);
        userRepositoryPort.save(savedSeller);
        return savedSeller;
    }

    @Override
    public Warehouse createWarehouse(String adminId, Warehouse warehouse) {
        if (warehouse == null) {
            throw new IllegalArgumentException("La información de la bodega no puede ser nula.");
        }
        if (warehouse.getName() == null || warehouse.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre de la bodega es obligatorio.");
        }
        return warehouseRepositoryPort.save(warehouse);
    }

    @Override
    public void changeUserStatus(String adminId, String targetUserId, String statusCode) {
        if (targetUserId == null || targetUserId.isBlank()) {
            throw new IllegalArgumentException("El identificador del usuario objetivo es obligatorio.");
        }
        if (statusCode == null || statusCode.isBlank()) {
            throw new IllegalArgumentException("El código de estado no puede estar vacío.");
        }

        User user = userRepositoryPort.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario", targetUserId));

        UserStatus targetStatus;
        if ("ACTIVE".equalsIgnoreCase(statusCode)) {
            targetStatus = UserStatus.ACTIVE;
        } else if ("INACTIVE".equalsIgnoreCase(statusCode)) {
            targetStatus = UserStatus.INACTIVE;
        } else if ("BLOCKED".equalsIgnoreCase(statusCode)) {
            targetStatus = UserStatus.BLOCKED;
        } else {
            throw new InvalidUserStatusException("Código de estado no reconocido: " + statusCode);
        }

        user.changeStatus(targetStatus);
        userRepositoryPort.update(user);
    }

    @Override
    public List<User> listUsers() {
        return userRepositoryPort.findAll();
    }

    @Override
    public List<Seller> listSellers() {
        return sellerRepositoryPort.findAll();
    }
}
