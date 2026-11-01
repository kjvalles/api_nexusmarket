package application.domain.ports.in;

import application.domain.models.AuditLog;
import application.domain.models.Order;
import application.domain.models.ProductStock;

import java.util.List;

/**
 * Puerto de entrada para operaciones de supervisión y auditoría (SUPERVISOR).
 */
public interface SupervisorPort {

    List<AuditLog> getAuditLogs(String supervisorId, String resourceFilter);

    List<Order> getAllOrders(String supervisorId);

    List<ProductStock> getInventorySummary(String supervisorId);
}
