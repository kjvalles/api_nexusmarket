package application.domain.ports.out;

import application.domain.models.Buyer;

import java.util.Optional;

/**
 * Puerto de salida para persistencia de Compradores.
 */
public interface BuyerRepositoryPort {

    Buyer save(Buyer buyer);

    Optional<Buyer> findById(String id);
}
