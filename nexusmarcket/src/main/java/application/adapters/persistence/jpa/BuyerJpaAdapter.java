package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.BuyerJpaEntity;
import application.adapters.persistence.jpa.mappers.BuyerJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaBuyerRepository;
import application.domain.models.Buyer;
import application.domain.ports.out.BuyerRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Adaptador de salida para persistencia de Compradores (MySQL vía Spring Data JPA).
 */
@Repository
@RequiredArgsConstructor
public class BuyerJpaAdapter implements BuyerRepositoryPort {

    private final SpringDataJpaBuyerRepository buyerRepository;

    @Override
    @Transactional
    public Buyer save(Buyer buyer) {
        if (buyer == null) {
            return null;
        }
        BuyerJpaEntity entity = BuyerJpaMapper.toEntity(buyer);
        BuyerJpaEntity saved = buyerRepository.save(entity);
        return BuyerJpaMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Buyer> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return buyerRepository.findById(id).map(BuyerJpaMapper::toDomain);
    }
}
