package application.domain.valueobjects;

import java.util.Objects;

public abstract class DomainCatalog {

    private final String code;
    private final String name;
    private final String description;

    protected DomainCatalog(String code, String name, String description) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("El código del catálogo no puede ser nulo o vacío.");
        }
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainCatalog that = (DomainCatalog) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), code);
    }

    @Override
    public String toString() {
        return code;
    }
}
