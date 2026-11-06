package application.domain.services.user;

import application.domain.enums.UserRole;
import application.domain.exceptions.DomainException;
import application.domain.models.Seller;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.UserStatus;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Servicio de dominio para registro y alta de comercios vendedores.
 */
@RequiredArgsConstructor
public class RegisterSellerService {

    private final UserRepositoryPort userRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;
    private final PasswordServicePort passwordServicePort;

    public Seller registerSeller(Seller seller, String initialPassword) {
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
}
