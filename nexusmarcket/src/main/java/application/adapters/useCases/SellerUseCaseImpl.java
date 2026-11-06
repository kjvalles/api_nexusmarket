package application.adapters.useCases;

import application.domain.models.Product;
import application.domain.models.ProductStock;
import application.domain.ports.in.SellerPort;
import application.domain.services.catalog.ConsultCatalogService;
import application.domain.services.catalog.CreateProductService;
import application.domain.services.catalog.PublishProductService;
import application.domain.services.catalog.SuspendProductService;
import application.domain.services.catalog.UpdateProductService;
import application.domain.services.inventory.ConsultInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso para operaciones del rol Vendedor (SELLER).
 * Delega toda la lógica de negocio en servicios de dominio sin acceso directo a repositorios.
 */
@Service
@RequiredArgsConstructor
public class SellerUseCaseImpl implements SellerPort {

    private final CreateProductService createProductService;
    private final UpdateProductService updateProductService;
    private final PublishProductService publishProductService;
    private final SuspendProductService suspendProductService;
    private final ConsultCatalogService consultCatalogService;
    private final ConsultInventoryService consultInventoryService;

    @Override
    public Product createProduct(String vendorId, Product product) {
        if (vendorId == null || vendorId.isBlank()) {
            throw new IllegalArgumentException("El identificador del vendedor no puede ser nulo o vacío.");
        }
        product.setVendorId(vendorId);
        return createProductService.createProduct(product);
    }

    @Override
    public Product updateProduct(String vendorId, Product product) {
        return updateProductService.updateProduct(vendorId, product);
    }

    @Override
    public void publishProduct(String vendorId, String productId) {
        publishProductService.publishProduct(vendorId, productId);
    }

    @Override
    public void suspendProduct(String vendorId, String productId) {
        suspendProductService.suspendProduct(vendorId, productId);
    }

    @Override
    public List<Product> getMyProducts(String vendorId) {
        return consultCatalogService.getProductsByVendor(vendorId);
    }

    @Override
    public List<ProductStock> getMyInventory(String vendorId) {
        return consultInventoryService.getInventoryByVendor(vendorId);
    }
}
