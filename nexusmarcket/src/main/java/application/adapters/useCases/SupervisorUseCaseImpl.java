package application.adapters.useCases;

import application.domain.models.AuditLog;
import application.domain.models.Order;
import application.domain.models.ProductStock;
import application.domain.ports.in.SupervisorPort;
import application.domain.services.audit.ConsultAuditLogService;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.order.ConsultOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso para operaciones de supervisión y auditoría (SUPERVISOR).
 * Delega 100% en servicios de dominio sin acceso directo a repositorios.
 */
@Service
@RequiredArgsConstructor
public class SupervisorUseCaseImpl implements SupervisorPort {

    private final ConsultAuditLogService consultAuditLogService;
    private final ConsultOrderService consultOrderService;
    private final ConsultInventoryService consultInventoryService;

    @Override
    public List<AuditLog> getAuditLogs(String supervisorId, String resourceFilter) {
        return consultAuditLogService.getAuditLogs(resourceFilter);
    }

    @Override
    public List<Order> getAllOrders(String supervisorId) {
        return consultOrderService.getAllOrders();
    }

    @Override
    public List<ProductStock> getInventorySummary(String supervisorId) {
        return consultInventoryService.getGlobalInventorySummary();
    }
}
