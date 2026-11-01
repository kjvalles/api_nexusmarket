package application.adapters.useCases;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedRoleOperationException;
import application.domain.models.Product;
import application.domain.models.ProductStock;
import application.domain.ports.in.SellerPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.ProductStockRepositoryPort;
import application.domain.services.catalog.CreateProductService;
import application.domain.services.catalog.PublishProductService;
import application.domain.services.catalog.UpdateProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso para operaciones del rol Vendedor (SELLER).
 */
@Service
@RequiredArgsConstructor
public class SellerUseCaseImpl implements SellerPort {

    private final CreateProductService createProductService;
    private final UpdateProductService updateProductService;
    private final PublishProductService publishProductService;
    private final ProductRepositoryPort productRepositoryPort;
    private final ProductStockRepositoryPort productStockRepositoryPort;

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
        if (product == null || product.getId() == null) {
            throw new IllegalArgumentException("El producto y su ID son obligatorios.");
        }
        validateVendorOwnership(vendorId, product.getId());
        product.setVendorId(vendorId);
        return updateProductService.updateProduct(product);
    }

    @Override
    public void publishProduct(String vendorId, String productId) {
        validateVendorOwnership(vendorId, productId);
        publishProductService.publishProduct(productId);
    }

    @Override
    public void suspendProduct(String vendorId, String productId) {
        Product product = validateVendorOwnership(vendorId, productId);
        product.suspend();
        productRepositoryPort.save(product);
    }

    @Override
    public List<Product> getMyProducts(String vendorId) {
        if (vendorId == null || vendorId.isBlank()) {
            throw new IllegalArgumentException("El identificador del vendedor no puede ser nulo o vacío.");
        }
        return productRepositoryPort.findByVendorId(vendorId);
    }

    @Override
    public List<ProductStock> getMyInventory(String vendorId) {
        List<Product> products = getMyProducts(vendorId);
        List<ProductStock> stockList = new ArrayList<>();
        for (Product product : products) {
            if (product.getId() != null) {
                stockList.addAll(productStockRepositoryPort.findByProductId(product.getId()));
            }
        }
        return stockList;
    }

    private Product validateVendorOwnership(String vendorId, String productId) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("El identificador del producto es obligatorio.");
        }
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Producto", productId));

        if (!vendorId.equals(product.getVendorId())) {
            throw new UnauthorizedRoleOperationException("No tiene autorización para modificar productos de otro vendedor.");
        }
        return product;
    }
}
