package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.SellerJpaEntity;
import application.adapters.persistence.jpa.mappers.SellerJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaSellerRepository;
import application.domain.models.Seller;
import application.domain.ports.out.SellerRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida para persistencia de Vendedores (MySQL vía Spring Data JPA).
 */
@Repository
@RequiredArgsConstructor
public class SellerJpaAdapter implements SellerRepositoryPort {

    private final SpringDataJpaSellerRepository sellerRepository;

    @Override
    @Transactional
    public Seller save(Seller seller) {
        if (seller == null) {
            return null;
        }
        SellerJpaEntity entity = SellerJpaMapper.toEntity(seller);
        SellerJpaEntity saved = sellerRepository.save(entity);
        return SellerJpaMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Seller> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return sellerRepository.findById(id).map(SellerJpaMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seller> findAll() {
        return sellerRepository.findAll().stream()
                .map(SellerJpaMapper::toDomain)
                .toList();
    }
}
