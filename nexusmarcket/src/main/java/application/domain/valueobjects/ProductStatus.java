package application.domain.valueobjects;

public final class ProductStatus extends DomainCatalog {

    public static final ProductStatus PUBLISHED = new ProductStatus(
            "PUBLISHED", "Published", "El producto es visible en el catálogo y puede ser comprado por compradores.");

    public static final ProductStatus SUSPENDED = new ProductStatus(
            "SUSPENDED", "Suspended", "El producto está temporalmente oculto del catálogo público.");

    public static final ProductStatus DISCONTINUED = new ProductStatus(
            "DISCONTINUED", "Discontinued", "El producto fue retirado definitivamente del catálogo y no puede reactivarse.");

    private ProductStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
