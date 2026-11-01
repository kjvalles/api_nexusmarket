package application.domain.ports.out;

import application.domain.models.Seller;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de Vendedores.
 */
public interface SellerRepositoryPort {

    Seller save(Seller seller);

    Optional<Seller> findById(String id);

    List<Seller> findAll();
}
