package application.domain.ports.out;

import application.domain.models.User;

import java.util.Optional;

/**
 * Puerto de salida para persistencia de Usuarios (MySQL).
 */
public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    Optional<User> findByIdentityDocument(String document);

    boolean existsByEmail(String email);

    boolean existsByIdentityDocument(String document);

    void update(User user);

    java.util.List<User> findAll();
}
