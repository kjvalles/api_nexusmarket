package application.domain.valueobjects;

public final class ProductType extends DomainCatalog {

    public static final ProductType PHYSICAL = new ProductType(
            "PHYSICAL", "Physical", "Producto físico que requiere inventario en bodega, empaque y envío al comprador.");

    public static final ProductType DIGITAL = new ProductType(
            "DIGITAL", "Digital", "Producto digital de entrega inmediata tras pago aprobado, sin envío ni movimiento de bodega.");

    private ProductType(String code, String name, String description) {
        super(code, name, description);
    }
}
