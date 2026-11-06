package application.adapters.useCases;

import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.AdministratorPort;
import application.domain.services.inventory.ManageWarehouseService;
import application.domain.services.user.ChangeUserStatusService;
import application.domain.services.user.ConsultUserService;
import application.domain.services.user.RegisterSellerService;
import application.domain.services.user.RegisterUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso para operaciones del rol Administrador (ADMINISTRATOR).
 * Delega toda la lógica de negocio en servicios de dominio sin acceso directo a repositorios.
 */
@Service
@RequiredArgsConstructor
public class AdministratorUseCaseImpl implements AdministratorPort {

    private final RegisterUserService registerUserService;
    private final RegisterSellerService registerSellerService;
    private final ChangeUserStatusService changeUserStatusService;
    private final ConsultUserService consultUserService;
    private final ManageWarehouseService manageWarehouseService;

    @Override
    public User registerUser(String adminId, User user, String initialPassword) {
        return registerUserService.registerUser(user, initialPassword);
    }

    @Override
    public User getUserById(String userId) {
        return consultUserService.getUserById(userId);
    }

    @Override
    public Seller registerSeller(String adminId, Seller seller, String initialPassword) {
        return registerSellerService.registerSeller(seller, initialPassword);
    }

    @Override
    public Warehouse createWarehouse(String adminId, Warehouse warehouse) {
        return manageWarehouseService.createWarehouse(warehouse);
    }

    @Override
    public void changeUserStatus(String adminId, String targetUserId, String statusCode) {
        changeUserStatusService.changeStatus(targetUserId, statusCode);
    }

    @Override
    public List<User> listUsers() {
        return consultUserService.listUsers();
    }

    @Override
    public List<Seller> listSellers() {
        return consultUserService.listSellers();
    }

    @Override
    public List<Warehouse> listWarehouses() {
        return manageWarehouseService.listWarehouses();
    }
}
