package application.domain.services.user;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Servicio de dominio para consultas de usuarios y vendedores registrados.
 */
@RequiredArgsConstructor
public class ConsultUserService {

    private final UserRepositoryPort userRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;

    public User getUserById(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("El identificador del usuario no puede estar vacío.");
        }
        return userRepositoryPort.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario", userId));
    }

    public List<User> listUsers() {
        return userRepositoryPort.findAll();
    }

    public List<Seller> listSellers() {
        return sellerRepositoryPort.findAll();
    }
}
