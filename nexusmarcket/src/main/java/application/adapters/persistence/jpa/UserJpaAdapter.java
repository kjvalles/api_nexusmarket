package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.UserJpaEntity;
import application.adapters.persistence.jpa.mappers.UserJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaUserRepository;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida para persistencia de Usuarios (MySQL vía Spring Data JPA).
 */
@Repository
@RequiredArgsConstructor
public class UserJpaAdapter implements UserRepositoryPort {

    private final SpringDataJpaUserRepository userRepository;

    @Override
    @Transactional
    public User save(User user) {
        if (user == null) {
            return null;
        }
        UserJpaEntity entity = UserJpaMapper.toEntity(user);
        UserJpaEntity saved = userRepository.save(entity);
        return UserJpaMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findById(id).map(UserJpaMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email).map(UserJpaMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByIdentityDocument(String document) {
        if (document == null || document.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByIdentityDocument(document).map(UserJpaMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return email != null && !email.isBlank() && userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdentityDocument(String document) {
        return document != null && !document.isBlank() && userRepository.existsByIdentityDocument(document);
    }

    @Override
    @Transactional
    public void update(User user) {
        if (user != null) {
            userRepository.save(UserJpaMapper.toEntity(user));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll().stream()
                .map(UserJpaMapper::toDomain)
                .toList();
    }
}
