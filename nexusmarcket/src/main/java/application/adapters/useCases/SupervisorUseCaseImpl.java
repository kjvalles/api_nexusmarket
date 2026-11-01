package application.adapters.useCases;

import application.domain.models.AuditLog;
import application.domain.models.Order;
import application.domain.models.ProductStock;
import application.domain.ports.in.SupervisorPort;
import application.domain.ports.out.AuditLogRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductStockRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso para operaciones de supervisión y auditoría (SUPERVISOR).
 */
@Service
@RequiredArgsConstructor
public class SupervisorUseCaseImpl implements SupervisorPort {

    private final AuditLogRepositoryPort auditLogRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ProductStockRepositoryPort productStockRepositoryPort;

    @Override
    public List<AuditLog> getAuditLogs(String supervisorId, String resourceFilter) {
        if (resourceFilter != null && !resourceFilter.isBlank()) {
            return auditLogRepositoryPort.findByEntity(resourceFilter, null);
        }
        return auditLogRepositoryPort.findAll();
    }

    @Override
    public List<Order> getAllOrders(String supervisorId) {
        return orderRepositoryPort.findAll();
    }

    @Override
    public List<ProductStock> getInventorySummary(String supervisorId) {
        return productStockRepositoryPort.findAll();
    }
}
