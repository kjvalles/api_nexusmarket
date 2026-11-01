package application.domain;

import application.domain.models.Buyer;
import application.domain.valueobjects.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias del Agregado User / Buyer (Control de Sesión e Invariantes)")
class UserDomainTest {

    @Test
    @DisplayName("Debe inicializar un usuario con versión de token 1 y estado activo según asignación")
    void shouldInitializeUserWithDefaultTokenVersion() {
        Buyer buyer = new Buyer();
        buyer.setId("buyer-1");
        buyer.setEmail("test@nexusmarket.com");
        buyer.setStatus(UserStatus.ACTIVE);

        assertEquals(1, buyer.getAuthTokenVersion());
        assertTrue(buyer.isActive());
    }

    @Test
    @DisplayName("Debe incrementar la versión del token al cambiar la contraseña para invalidar sesiones previas")
    void shouldBumpTokenVersionOnChangePassword() {
        Buyer buyer = new Buyer();
        buyer.setPassword("oldHashedPassword");
        assertEquals(1, buyer.getAuthTokenVersion());

        buyer.changePassword("newHashedPassword");

        assertEquals("newHashedPassword", buyer.getPassword());
        assertEquals(2, buyer.getAuthTokenVersion());
    }

    @Test
    @DisplayName("Debe incrementar la versión del token al cambiar el estado del usuario")
    void shouldBumpTokenVersionOnChangeStatus() {
        Buyer buyer = new Buyer();
        buyer.setStatus(UserStatus.ACTIVE);
        assertEquals(1, buyer.getAuthTokenVersion());

        buyer.changeStatus(UserStatus.BLOCKED);

        assertEquals(UserStatus.BLOCKED, buyer.getStatus());
        assertFalse(buyer.isActive());
        assertEquals(2, buyer.getAuthTokenVersion());
    }

    @Test
    @DisplayName("Debe gestionar el saldo virtual del comprador y rechazar débitos mayores al saldo disponible")
    void shouldManageBuyerVirtualBalance() {
        Buyer buyer = new Buyer();
        assertEquals(BigDecimal.ZERO, buyer.getVirtualBalance());

        buyer.creditBalance(new BigDecimal("150.00"));
        assertEquals(new BigDecimal("150.00"), buyer.getVirtualBalance());

        buyer.debitBalance(new BigDecimal("50.00"));
        assertEquals(new BigDecimal("100.00"), buyer.getVirtualBalance());

        assertThrows(RuntimeException.class, () -> buyer.debitBalance(new BigDecimal("200.00")));
    }
}
