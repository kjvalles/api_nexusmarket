package application.domain.services.auth;

import application.domain.enums.UserRole;
import application.domain.exceptions.DomainException;
import application.domain.models.Buyer;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.UserStatus;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Servicio de dominio para el registro de nuevos compradores.
 */
@RequiredArgsConstructor
public class RegisterBuyerService {

    private final UserRepositoryPort userRepositoryPort;
    private final BuyerRepositoryPort buyerRepositoryPort;
    private final PasswordServicePort passwordServicePort;

    public Buyer registerBuyer(Buyer buyer, String rawPassword) {
        if (buyer == null) {
            throw new IllegalArgumentException("La información del comprador no puede ser nula.");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        if (buyer.getEmail() == null || buyer.getEmail().isBlank()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        }
        if (buyer.getIdentityDocument() == null || buyer.getIdentityDocument().isBlank()) {
            throw new IllegalArgumentException("El documento de identidad es obligatorio.");
        }

        if (userRepositoryPort.existsByEmail(buyer.getEmail())) {
            throw new DomainException("El correo electrónico ya se encuentra registrado: " + buyer.getEmail());
        }
        if (userRepositoryPort.existsByIdentityDocument(buyer.getIdentityDocument())) {
            throw new DomainException("El documento de identidad ya se encuentra registrado: " + buyer.getIdentityDocument());
        }

        buyer.setPassword(passwordServicePort.encode(rawPassword));
        buyer.setRole(UserRole.BUYER);
        buyer.setStatus(UserStatus.ACTIVE);
        buyer.setAuthTokenVersion(1);
        buyer.setCreatedAt(LocalDateTime.now());
        buyer.setUpdatedAt(LocalDateTime.now());

        Buyer savedBuyer = buyerRepositoryPort.save(buyer);
        userRepositoryPort.save(savedBuyer);
        return savedBuyer;
    }
}
