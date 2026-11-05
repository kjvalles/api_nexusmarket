package application.domain.ports.in;

import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;

import java.util.List;

/**
 * Puerto de entrada para casos de uso del rol Administrador (ADMINISTRATOR).
 */
public interface AdministratorPort {

    User registerUser(String adminId, User user, String initialPassword);

    User getUserById(String userId);

    Seller registerSeller(String adminId, Seller seller, String initialPassword);

    Warehouse createWarehouse(String adminId, Warehouse warehouse);

    void changeUserStatus(String adminId, String targetUserId, String statusCode);

    List<User> listUsers();

    List<Seller> listSellers();

    List<Warehouse> listWarehouses();
}
